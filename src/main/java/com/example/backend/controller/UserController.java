package com.example.backend.controller;

import com.example.backend.config.JwtTokenProvider;
import com.example.backend.dto.Login;
import com.example.backend.dto.Users;
import com.example.backend.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // 해당 오리진에서의 요청을 허용
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // application.properties에 정의된 값들을 주입받음
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    // 회원가입 기능
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody Users user) {
        System.out.println("user ======" + user.toString());

        userService.signUp(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 로그인 기능
    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody Login.req req) {
        System.out.println("user ======" + req.toString());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUserId(), req.getPassword())
            );

            String token = jwtTokenProvider.generateToken(authentication, "regular"); // 일반로그인
            return ResponseEntity.ok(new Login.res(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    // 네이버 로그인
    @GetMapping("/oauth/naver")
    public ResponseEntity<?> redirectNaverLogin() {
        String state = UUID.randomUUID().toString();

        String naverUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="
                + naverClientId + "&redirect_uri=" + URLEncoder.encode(naverRedirectUri, StandardCharsets.UTF_8)
                + "&state=" + state;

        // properties 파일에서 가져온 clientId와 redirectUri로 URL 생성
//        String naverUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="
//                + naverClientId + "&redirect_uri=" + URLEncoder.encode(naverRedirectUri, StandardCharsets.UTF_8)
//                + "&state=" + state;

        System.out.println(naverUrl);

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", naverUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth2/callback/naver")
    public ResponseEntity<?> handleNaverCallback2(@RequestParam String code, @RequestParam String state) {
        System.out.println("파라미터 조회 ==> " + code);
        System.out.println("파라미터 조회 ==> " + state);

        String tokenUrl = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code"
                + "&client_id=" + naverClientId
                + "&client_secret=" + naverClientSecret
                + "&code=" + code
                + "&state=" + state;

        // RestTemplate을 이용해 액세스 토큰 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, null, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // 액세스 토큰 추출
            String responseBody = response.getBody();
            System.out.println("토큰 응답: " + responseBody);

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode tokenJson = objectMapper.readTree(responseBody);
                String accessToken = tokenJson.get("access_token").asText();
                System.out.println("액세스 토큰: " + accessToken);

                // 액세스 토큰을 이용해 사용자 정보 요청
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + accessToken);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                        "https://openapi.naver.com/v1/nid/me",
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                // 사용자 정보 응답 처리
                String userInfo = userInfoResponse.getBody();
                JsonNode userInfoJson = objectMapper.readTree(userInfo);

                // 사용자 ID 가져오기
                String userId = userInfoJson.get("response").get("id").asText();

                // 사용자 존재 여부 확인
                Users existingUser = userService.findByUserId(userId);
                if (existingUser == null) {
                    // 미가입자면 회원 등록 처리
                    String cleanBirthday = userInfoJson.get("response").get("birthday").asText().replace("-", "");
                    String cleanMobile = userInfoJson.get("response").get("mobile").asText().replace("-", "");

                    Users newUser = new Users();
                    newUser.setUserId(userId);
                    newUser.setEmail(userInfoJson.get("response").get("email").asText());
                    newUser.setName(userInfoJson.get("response").get("name").asText());
                    newUser.setMobile(cleanMobile);
                    newUser.setBIRTH(userInfoJson.get("response").get("birthyear").asText() + cleanBirthday);
                    newUser.setGENDER(userInfoJson.get("response").get("gender").asText());

                    // 네이버 로그인으로 등록된 사용자이므로 비밀번호를 설정하지 않음
                    newUser.setSocialLogin(true);

                    // 회원 등록
                    userService.signUp(newUser);

                    // 새로 가입된 사용자로 인증 생성
                    existingUser = newUser;
                }

                // JWT 토큰 생성 (일반 로그인과 동일)
                Authentication authentication = new UsernamePasswordAuthenticationToken(existingUser, null, new ArrayList<>());
                String token = jwtTokenProvider.generateToken(authentication, "naver");

                // 토큰을 클라이언트에게 응답으로 보냄

                System.out.println(ResponseEntity.ok(Map.of("token", token)));
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "http://localhost:3000/oauth2/callback/naver?token=" + token)
                        .build();

//                return ResponseEntity.ok(Map.of("token", token));  // 단순 JSON 응답

//                return ResponseEntity.status(HttpStatus.FOUND)
//                        .header(HttpHeaders.LOCATION, "http://localhost:3000/oauth2/callback/naver?token=" + token)
//                        .build();

//                return ResponseEntity.ok(new Login.res(token));

//                return ResponseEntity.ok("네이버 로그인 성공")

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("JSON 처리 오류 발생");
            }

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("네이버 로그인 실패");
        }
    }

    // 카카오 로그인
    @GetMapping("/oauth/kakao")
    public ResponseEntity<?> redirectKakaoLogin() {
        // properties 파일에서 가져온 clientId와 redirectUri로 URL 생성
        String kakaoUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                + kakaoClientId + "&redirect_uri=" + URLEncoder.encode(kakaoRedirectUri, StandardCharsets.UTF_8);

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", kakaoUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> handleKakaoCallback(@RequestParam String code) {
        // 카카오 토큰 발급 및 사용자 정보 요청 처리
        // 토큰 발급 후 클라이언트로 필요한 정보를 리턴
        return ResponseEntity.ok("카카오 로그인 성공");
    }
}
package com.board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Configuration
public class SecurityConfig {
    // openssl rand -base64 64 -> 안전한 랜덤 64바이트 만들어서 텍스트로 출력
    // PBKDF2도 salt + hash를 하나의 문자열로 합쳐서 DB에 저장
    // BCrypt, Argon2
    @Bean
    public PasswordEncoder passwordEncoder(@Value("${security.password.secret}") String secret){
        // return new BCryptPasswordEncoder();
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(
                secret,
                16,  // salt 길이 (바이트), 키 미사용
                310000,    // 반복 횟수 (권장값) = 0.03~0.1초
                512        // 해시 출력 비트 길이 (int 타입)
        );
        encoder.setAlgorithm(
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512 // 어떤 해시 함수를 반복할지, 해시 출력 비트 길이랑 짝 맞추기 (효율)
        );
        return encoder;
    }
}

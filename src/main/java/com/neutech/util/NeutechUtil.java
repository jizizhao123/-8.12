// src/main/java/com/neutech/util/NeutechUtil.java
package com.neutech.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.neutech.entity.Users;

public class NeutechUtil {
    private static final String KEY = "jzz";

    public static String encode(Users users) {
        return JWT.create()
                .withClaim("id", users.getId())
                .withClaim("name", users.getName())
                .withClaim("email", users.getEmail())
                .withClaim("major", users.getMajor())
                .withClaim("number", users.getNumber())
                .withClaim("score", users.getScore() != null ? users.getScore().doubleValue() : null)
                .withClaim("role", users.getRole())
                .withClaim("isAdmin", users.getIsAdmin())
                .withClaim("avatarUrl", users.getAvatarUrl())
                .sign(Algorithm.HMAC256(KEY));
    }

    public static Users decode(String token) {
        try {
            DecodedJWT verify = JWT.require(Algorithm.HMAC256(KEY)).build().verify(token);

            Users user = new Users();
            user.setId(verify.getClaim("id").asInt());

            String name = verify.getClaim("name").asString();
            if (name != null) user.setName(name);

            String email = verify.getClaim("email").asString();
            if (email != null) user.setEmail(email);

            String major = verify.getClaim("major").asString();
            if (major != null) user.setMajor(major);

            String number = verify.getClaim("number").asString();
            if (number != null) user.setNumber(number);

            Double score = verify.getClaim("score").asDouble();
            if (score != null) user.setScore(score.floatValue());

            String role = verify.getClaim("role").asString();
            if (role != null) user.setRole(role);

            Boolean isAdmin = verify.getClaim("isAdmin").asBoolean();
            if (isAdmin != null) user.setIsAdmin(isAdmin);

            String avatarUrl = verify.getClaim("avatarUrl").asString();
            if (avatarUrl != null) user.setAvatarUrl(avatarUrl);

            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

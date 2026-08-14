package com.gp.framework.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.gp.common.core.result.Result;
import com.gp.common.utils.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "验证码")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private RedisUtils redisUtils;

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final long EXPIRE_SECONDS = 120;

    @Operation(summary = "生成验证码")
    @GetMapping("/captchaImage")
    public Result<Map<String, Object>> captchaImage() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String captchaKey = "captcha:" + uuid;

        Random random = new Random();
        int a = random.nextInt(20);
        int b = random.nextInt(20);
        int result = a + b;
        String captchaCode = a + " + " + b + " = ?";

        redisUtils.set(captchaKey, String.valueOf(result), EXPIRE_SECONDS, TimeUnit.SECONDS);

        String base64 = generateCaptchaImage(captchaCode);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("uuid", uuid);
        resultMap.put("img", "data:image/png;base64," + base64);
        return Result.success(resultMap);
    }

    private String generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.BLACK);
        g.drawString(code, 10, 30);

        Random random = new Random();
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 6; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.fillOval(x, y, 2, 2);
        }

        g.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            javax.imageio.ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("生成验证码失败", e);
        }
    }

}
package com.fourctc.tuyendungthongminh_be.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        String role = null;

        // 1. Kiểm tra header Authorization có Bearer token không
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(token);
                role = jwtUtil.extractRole(token); // đã trả về "ROLE_ADMIN", "ROLE_HR", ...
            } catch (JwtException | IllegalArgumentException e) {
                // Token không hợp lệ hoặc hết hạn → không làm gì, để filter chain tiếp tục
                // (sẽ bị chặn ở SecurityConfig nếu route yêu cầu auth)
            }
        }

        // 2. Nếu tìm thấy email và chưa có authentication trong SecurityContext
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(token)) {

                // Quan trọng: role từ token phải có dạng "ROLE_ADMIN", "ROLE_HR", ...
                // → Spring Security sẽ tự hiểu .hasRole("ADMIN") = tìm authority "ROLE_ADMIN"
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,  // credentials (password) = null vì dùng JWT
                                authorities
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Đưa thông tin user vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Debug (có thể xóa sau khi ổn định)
                System.out.println("[JWT Filter] Đã xác thực thành công: " + email + " | Role: " + role);
            }
        }

        // Tiếp tục chuỗi filter
        filterChain.doFilter(request, response);
    }

    /**
     * Bỏ qua filter cho các endpoint public (không cần kiểm tra token)
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();

        return path.equals("/users/login") ||
                path.equals("/users/register") ||
                path.equals("/users/verify") ||
                path.equals("/users/request-reset") ||
                path.equals("/users/reset-password") ||
                path.equals("/users/validate-reset-token") ||
                path.startsWith("/job-categories/popular") ||
                path.startsWith("/companies/public") ||
                path.startsWith("/companies/featured") ||
                path.startsWith("/jobs/search") ||
                path.startsWith("/jobs/latest") ||
                path.startsWith("/jobs/approved") ||
                path.startsWith("/uploads/");
    }
}
package br.com.viniciusborba.tododolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.viniciusborba.tododolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
      throws ServletException, IOException {

        var servletPath = req.getServletPath();

        if (servletPath.startsWith("/tasks/")) {
          var authorization = req.getHeader("Authorization");

          var authEncoded = authorization.substring("Basic".length()).trim();

          byte[] authDecode = Base64.getDecoder().decode(authEncoded);

          var authString = new String(authDecode);

          String[] credentials = authString.split(":");
          String username = credentials[0];
          String password = credentials[1];

          var user = this.userRepository.findByUsername(username);
          if(user == null) {
            res.sendError(401, "Not allowed");
          } else {
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if (passwordVerify.verified) {
              req.setAttribute("userId", user.getId());
              filterChain.doFilter(req, res);
            } else {
              res.sendError(401);
            }
          }
        } else {
          filterChain.doFilter(req, res);
        }
  }
}

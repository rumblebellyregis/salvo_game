package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	private Date date = new Date();


	@Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
	public CommandLineRunner initData(PlayerRepository repository, GameRepository gamerepository, GamePlayerRepository gameplayerrepository, ShipRepository ships, SalvoRepository solvoes, ScoreRepository scores){
		return (args) -> {
			Player p1=new Player("j.bauer@ctu.gov","123456");
			repository.save(p1);
			Player p2=new Player("c.obianr@ctu.gov","123456");
			repository.save(p2);
			Player p3=new Player("kim_bauer@gmail.com","123456");
			repository.save(p3);
			Player p4=new Player("t.almeida@ctu.gov","213456");
			repository.save(p4);
			Game g1=new Game();
			gamerepository.save(g1);
			Game g2=new Game();
			gamerepository.save(g2);
			Game g3=new Game();
			gamerepository.save(g3);
			Game g4=new Game();
			gamerepository.save(g4);
			Game g5=new Game();
			gamerepository.save(g5);
			GamePlayer gp1=new GamePlayer(g1,p1);
			gameplayerrepository.save(gp1);
			GamePlayer gp2=new GamePlayer(g1,p2);
			gameplayerrepository.save(gp2);
			GamePlayer gp3=new GamePlayer(g3,p4);
			gameplayerrepository.save(gp3);
			GamePlayer gp4=new GamePlayer(g2,p3);
			gameplayerrepository.save(gp4);
            GamePlayer gp5=new GamePlayer(g3,p1);
            gameplayerrepository.save(gp5);
            GamePlayer gp6=new GamePlayer(g4,p3);
            gameplayerrepository.save(gp6);
            GamePlayer gp7=new GamePlayer(g4,p2);
            gameplayerrepository.save(gp7);




			ArrayList<String> locations = new ArrayList<String>(
					Arrays.asList("h1","h2","h3"));
			String sname="destroyer";
			Ship s1=new Ship(sname,locations,gp1);
			ships.save(s1);
			ArrayList<String> locations2 = new ArrayList<String>(
					Arrays.asList("g1","g2","g3","g4"));
			String sname2="destroyer";
			Ship s2=new Ship(sname2,locations2,gp3);
			ships.save(s2);
			ArrayList<String> locations3 = new ArrayList<String>(
					Arrays.asList("a1","a2","a3"));
			String sname3="patrolBoat";
			Ship s3=new Ship(sname3,locations3,gp1);
			ships.save(s3);
			ArrayList<String> locations4 = new ArrayList<String>(
					Arrays.asList("b1","b2"));
			String sname4="patrolBoat";
			Ship s4=new Ship(sname4,locations4,gp3);
			ships.save(s4);

			ArrayList<String> locations5 = new ArrayList<String>(
					Arrays.asList("b1","c1"));
			String sname5="patrolBoat";
			Ship s5=new Ship(sname5,locations5,gp2);
			ships.save(s5);
			ArrayList<String> locations6 = new ArrayList<String>(
					Arrays.asList("b1","c2"));

			Salvo sa1=new Salvo(gp1,3,locations6);
			solvoes.save(sa1);
			ArrayList<String> locations7 = new ArrayList<String>(
					Arrays.asList("b3","c1"));

			Salvo sa2=new Salvo(gp1,4,locations7);
			solvoes.save(sa2);
			ArrayList<String> locations8 = new ArrayList<String>(
					Arrays.asList("b4","a3"));

			Salvo sa3=new Salvo(gp2,3,locations8);
			solvoes.save(sa3);
			ArrayList<String> locations9 = new ArrayList<String>(
					Arrays.asList("b10","a10"));

			Salvo sa4=new Salvo(gp2,2,locations9);
			solvoes.save(sa4);
			Date d1=new Date();
			Score sc1=new Score(p1,g1,d1,1.0);
			scores.save(sc1);
			Score sc2=new Score(p3,g2,d1,1.0);
			scores.save(sc2);
			Score sc3=new Score(p4,g3,d1,0.5);
			scores.save(sc3);
			Score sc4=new Score(p1,g2,d1,0.0);
			scores.save(sc4);
		};
	}


}
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassWord(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}
@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/web/**").permitAll()
                .antMatchers("/api/current/**").permitAll()
                .antMatchers("/api/players").permitAll()
                .antMatchers("/**").hasAuthority("USER")
                .and()
                .formLogin();

        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/login");




        http.logout().logoutUrl("/api/logout");

        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());



            }
    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
        }









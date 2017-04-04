package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

@RestController
class DemoController {
    @RequestMapping("/launch")
    String launch() throws IOException{

	Process _process = new ProcessBuilder().command(
							"./main",
							"-mod","client",
							"-aid","12345",
							"-hst","ws://localhost:8990/agent",
							"-lpt","7990",
							"-tid","23456",
							"-dbg",
							"-tkn","dGVzdC1jaGlh").inheritIO().start();
	

	Thread _thd =new Thread(new ThreadProvisioner(_process));
	_thd.start();
	
	return "EC launched.";
    }

    @RequestMapping("/getData")
    String getData(){
	DriverManagerDataSource ds = new DriverManagerDataSource();
	ds.setDriverClassName("org.postgresql.Driver");
	ds.setUrl("jdbc:postgresql://localhost:7990/postgres");
	ds.setUsername("postgres");
	ds.setPassword("sa");

	JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

	try {
	    String chiadata=jdbcTemplate.queryForObject("select code from test fetch first 1 rows only;", String.class);
	    return chiadata;
	}
	catch(Throwable ex){
	    return ex.getMessage();
	}

    }
    
    private class ThreadProvisioner implements Runnable {

        private Process _proc;
        public ThreadProvisioner(Process proc){
            _proc=proc;
	}

        @Override
	public void run() {

            try{
                final int exCode=_proc.waitFor();
                System.out.println("Exit Code:"+exCode);
            } catch(Throwable ex){
		System.out.println(ex);
	    }

	}

    }
}



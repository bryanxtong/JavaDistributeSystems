package com.example.grpcserver;

import org.lognet.springboot.grpc.security.GrpcSecurity;
import org.lognet.springboot.grpc.security.GrpcSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.sample.proto.SimpleGrpc;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.lognet.springboot.grpc.security.jwt.JwtAuthProviderFactory;

@Configuration
public class GrpcSecurityConfiguration extends GrpcSecurityConfigurerAdapter {
    @Autowired
    private JwtDecoder jwtDecoder;
    @Override
    public void configure(GrpcSecurity builder) throws Exception {
        builder.authorizeRequests()
                .methods(SimpleGrpc.getSayHelloMethod()).hasAnyAuthority("SCOPE_profile")
                .methods(SimpleGrpc.getBiStreamHelloMethod()).hasAnyAuthority("SCOPE_profile")
                .methods(SimpleGrpc.getClientStreamHelloMethod()).hasAnyAuthority("SCOPE_profile")
                .methods(SimpleGrpc.getStreamHelloMethod()).hasAnyAuthority("SCOPE_profile")
                .and()
                .authenticationProvider(JwtAuthProviderFactory.forAuthorities(jwtDecoder));
    }
}
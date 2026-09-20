package com.ttrp.manager.controller;


import com.ttrp.manager.helper.authentication.annotation.IsAdmin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@IsAdmin
public class AdminController {

}

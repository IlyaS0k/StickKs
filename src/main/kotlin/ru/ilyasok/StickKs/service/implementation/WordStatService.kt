package ru.ilyasok.StickKs.service.implementation;

import org.antlr.v4.runtime.misc.IntegerList;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/")
class WordStatService {

    @GetMapping()
    fun hello(model: Model): String {
        return "xiao"
    }


}

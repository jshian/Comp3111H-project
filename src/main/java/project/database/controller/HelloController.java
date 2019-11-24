package project.database.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HelloController {
    @ResponseBody
    @GetMapping(value = "hello")
    public String hello(){
        return "hello world";
    }

    @RequestMapping(value = "/top10",method = RequestMethod.GET)
    public ModelAndView top10(){
        ModelAndView modelAndView =new ModelAndView();
        modelAndView.setViewName("Leaderboard");
        return modelAndView;
    }
}

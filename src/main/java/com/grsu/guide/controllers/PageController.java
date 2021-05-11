package com.grsu.guide.controllers;

import com.grsu.guide.domain.Element;
import com.grsu.guide.domain.Page;
import com.grsu.guide.service.ElementService;
import com.grsu.guide.service.PageService;
import com.grsu.guide.service.SmtpMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
public class PageController {

    private final SmtpMailSender mailSender;
    private final PageService pageService;
    private final ElementService elementService;

    @Autowired
    public PageController(SmtpMailSender mailSender, PageService pageService, ElementService elementService){
        this.mailSender = mailSender;
        this.pageService = pageService;
        this.elementService = elementService;
    }

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/")
        public String main( Model model){
        List<Page> pages = (List<Page>) pageService.GetAllPages();
        List <Element> sortedList = new ArrayList<>(pages.get(0).getElements());
        sortedList.sort(Comparator.comparing(Element::getId));
        model.addAttribute("elements",sortedList);
        model.addAttribute("pages", pages);
        model.addAttribute("page", pages.get(0));
        return "home";
    }

    @GetMapping("/{namePage}")
        public String GetPage(@PathVariable(value = "namePage") String namePage, Model model){
       Page page = pageService.GetPage(namePage);
       List<Page> pages = (List<Page>) pageService.GetAllPages();
        model.addAttribute("page", page);
        model.addAttribute("pages", pages);
        return "main";
    }


    @PostMapping("/{namePage}/add")
    public String AddPage(@RequestParam(required = false) String newNamePage,
                          @PathVariable String namePage){
        Page page = new Page();
        page.setNamePage(newNamePage);
        pageService.AddPage(page);
        return "redirect:/{newNamePage}";
    }


    @PostMapping("/{namePage}/add_el")
    public String AddElement(@RequestParam(required=false) String value,
                             @RequestParam(required=false) String type,
                             @PathVariable String namePage,
                             @RequestParam(value = "file", required = false) MultipartFile file
                             ) throws IOException {
        Page page = pageService.GetPage(namePage);
        Element element = new Element();

        if(file !=null && !file.getOriginalFilename().isEmpty()){
             element.setValue(elementService.UploadElement(file,uploadPath));
        }
        else{
            if(type.equals("text")) {
                element.setValue(value);
            }
            else
            {
                return "redirect:/{namePage}";
            }
        }
        element.setType(type);
        Set<Element> elements = page.getElements();
        elements.add(element);
        page.setElements(elements);
        pageService.AddPage(page);
        return "redirect:/{namePage}";
    }



    @PostMapping("/Feedback")
    public String Feedback(@RequestParam(required = false) String name,
                           @RequestParam(required = false) String mail,
                           @RequestParam(required = false) String message){
        String messageTo = String.format(
                "Email: %s \n" +
                        "Name: %s \n" +
                       "%s",
                mail,name,message

        );
        mailSender.send("Feedback", messageTo);
        return "redirect:/FeedBack";
    }


}

package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.service.FixedOverPrepayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2026/3/18
 */
@RestController
@RequestMapping("/fixOverPre")
public class FixOverPrepayController {

    @Autowired
    private FixedOverPrepayService fixedOverPrepayService;

    @PostMapping("/import")
    public RestResult<String> importExcel(@RequestParam String filePath){
        fixedOverPrepayService.importExcel(filePath);
        return RestResult.ok("操作成功");
    }
}

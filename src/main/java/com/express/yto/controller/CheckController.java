package com.express.yto.controller;

import com.express.yto.dto.CheckInput;
import com.express.yto.dto.RestResult;
import com.express.yto.service.CheckService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2025/10/11
 */
@RestController
@RequestMapping("/check")
public class CheckController {

    @Autowired
    private CheckService checkService;

    @PostMapping("/checkAmount")
    @ApiOperation("检查总金额")
    public RestResult<List<String>> checkAmount(@RequestBody CheckInput input){
        return checkService.checkAmount(input.getReadPath(), input.getCheckPath());
    }
}

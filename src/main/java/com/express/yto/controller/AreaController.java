package com.express.yto.controller;

import com.express.yto.dto.AreaDTO;
import com.express.yto.dto.AreaListDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.service.AreaService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2026/4/23
 */
@RestController
@RequestMapping("/area")
@ApiOperation("区域管理")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @GetMapping("/list")
    public RestResult<List<AreaListDTO>> getList(){
        return RestResult.ok(areaService.getList());
    }
}

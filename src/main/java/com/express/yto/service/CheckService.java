package com.express.yto.service;

import com.express.yto.dto.RestResult;
import java.util.List;

/**
 * @author Detective
 * @date Created in 2025/10/11
 */
public interface CheckService {

    RestResult<List<String>> checkAmount(String readPath, String checkPath);
}

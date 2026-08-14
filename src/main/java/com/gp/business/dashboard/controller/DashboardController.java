package com.gp.business.dashboard.controller;

import java.util.Map;

import com.gp.business.dashboard.service.DashboardService;
import com.gp.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据大屏")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "大屏全量统计数据")
    @GetMapping("/screen")
    public Result<Map<String, Object>> screen() {
        return Result.success(dashboardService.screenData());
    }

}

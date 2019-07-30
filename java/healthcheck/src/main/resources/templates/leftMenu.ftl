<div class="aside">
    <i class="aside_bg"></i>
    <div class="aside_main">
        <dl class="aside_dl">
            <dt class="aside_dt jsBar" <#if menuKey gt 0 && menuKey lte 10>open="open"</#if> >Application</dt>
            <dd class="aside_dd"><a href="/healthcheck/appNodes" class="aside_nav <#if menuKey==1>active</#if>"><i class="icon icon_nav_ux fas fa-transgender-alt"></i>应用节点</a></dd>
            <dd class="aside_dd"><a href="/healthcheck/appNginxConfTpls" class="aside_nav <#if menuKey==2>active</#if>"><i class="icon icon_nav_ux fas fa-transgender-alt"></i>模板列表</a></dd>
            <dd class="aside_dd"><a href="/healthcheck/serverHealthCheckConfig" class="aside_nav <#if menuKey==3>active</#if>"><i class="icon icon_nav_ux fas fa-cog"></i>应用设置</a></dd>
            <dd class="aside_dd"><a href="/healthcheck/springbootActuatorLogSetting" class="aside_nav <#if menuKey==4>active</#if>"><i class="icon icon_nav_ux fas fa-archive"></i>SpringBoot日志</a></dd>
        </dl>
        <dl class="aside_dl">
            <dt class="aside_dt jsBar" <#if menuKey gt 10 && menuKey lte 20>open="open"</#if> >System</dt>
            <dd class="aside_dd"><a href="/healthcheck/serverHkRobots" class="aside_nav <#if menuKey==11>active</#if>"><i class="icon icon_nav_ux fas fa-robot"></i>检测Robot</a></dd>
            <dd class="aside_dd"><a href="/healthcheck/lbClassicStatusFrame" class="aside_nav <#if menuKey==12>active</#if>""><i class="icon icon_nav_ux fas fa-archive"></i>日志记录</a></dd>
        </dl>
        <div class="aside_divide"></div>
        <dl class="aside_dl">
            <dd><a href="https://github.com/JerryXia/CodeFragment/tree/master/java/healthcheck" class="aside_nav" target="_blank"><i class="icon icon_nav_ux fas fa-wrench"></i>关于</a></dd>
        </dl>
    </div>
</div>
<#include "../header.ftl">
<#include "../commonStyles.ftl">
</head>
<body>
    <#include "../logoArea.ftl">

<div class="main">
    <#include "../leftMenu.ftl">
    <!-- 主内容 -->
    <section>
        <div class="section_main">
            <div class="section_auto">
                <div class="mt30">
                    <table class="ui-table">
                        <thead>
                            <tr>
                              <th scope="col">Server-Manager</th>
                              <th scope="col">状态</th>
                              <th scope="col">Worker</th>
                              <th scope="col">状态</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#list managers?keys as mkey>
                                <#list managers[mkey].serverCheckWorkers as worker>
                                    <tr>
                                        <#if worker?index == 0>
                                            <td rowspan="${managers[mkey].serverCheckWorkers?size}">${mkey}</td>
                                            <td rowspan="${managers[mkey].serverCheckWorkers?size}">${managers[mkey].status}</td>
                                        </#if>
                                        <td>${worker.name}</td>
                                        <td>${worker.state}</td>
                                    </tr>
                                </#list>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </section>
</div>

<#include "../commonScripts.ftl">
<script>

</script>
<#include "../footer.ftl">
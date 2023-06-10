<#include "header.ftl">
<#include "html5shiv.ftl">
<#include "common_styles.ftl">
<style>

</style>
</head>
<body>
    <#include "nav.ftl">

<div class="container-fluid" id="app" v-cloak>
    <div class="row">
        <div class="col-md-12">

            <ul class="tree tree-lines tree-angles" id="beansTree"></ul>
        </div>
    </div>
</div>

<input type="hidden" id="hid_val" data-dashboardpathprefix="${viewData.page_global_dashboardPathPrefix}" value="" />
<#include "common_scripts.ftl">
<#include "footer.ftl">
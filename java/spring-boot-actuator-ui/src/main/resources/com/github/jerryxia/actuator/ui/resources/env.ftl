<#include "header.ftl">
<#include "html5shiv.ftl">
<#include "common_styles.ftl">
</head>
<body>
    <#include "nav.ftl">

<div class="container-fluid" id="app" v-cloak>
    <div class="row">
        <div class="col-md-12" v-for="applicationConfig in applicationConfigs">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <div class="panel-title">{{applicationConfig.key}}</div>
                </div>
                <div class="panel-body">
                    <table class="table table-hover table-condensed table-fixed table-info">
                        <tbody>
                            <tr v-for="item in applicationConfig.value">
                                <td class="w400" v-bind:title="item.key"><small>{{item.key}}</small></td>
                                <td v-bind:title="item.value"><code><span class="atv">{{item.value}}</span></code></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-6">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <div class="panel-title">System Properties</div>
                </div>
                <div class="panel-body">
                    <table class="table table-hover table-condensed table-fixed table-info">
                        <tbody>
                            <tr v-for="item in systemProperties">
                                <td class="w250" v-bind:title="item.key"><small>{{item.key}}</small></td>
                                <td v-bind:title="item.value"><code><span class="atv">{{item.value}}</span></code></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <div class="panel-title">System Environment</div>
                </div>
                <div class="panel-body">
                    <table class="table table-hover table-condensed table-fixed table-info">
                        <tbody>
                            <tr v-for="item in systemEnvironment">
                                <td class="w250" v-bind:title="item.key"><small>{{item.key}}</small></td>
                                <td v-bind:title="item.value"><code><span class="atv">{{item.value}}</span></code></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<input type="hidden" id="hid_val" data-dashboardpathprefix="${viewData.page_global_dashboardPathPrefix}" value="" />
<#include "common_scripts.ftl">
<#include "footer.ftl">
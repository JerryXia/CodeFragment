<#include "header.ftl">
<#include "html5shiv.ftl">
<#include "common_styles.ftl">
</head>
<body>
    <#include "nav.ftl">

<div class="container-fluid" id="app" v-cloak>
    <div class="row">
        <div class="col-md-12">
            <table class="table table-bordered table-condensed table-hover table-fixed">
                <caption>${viewData.title}</caption>
                <thead>
                    <tr>
                        <th>Path</th>
                        <th>Source</th>
                        <th class="w200">Bean</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="mapping in customMappings">
                        <td><p v-bind:title="mapping.path">{{mapping.path}}</p></td>
                        <td><p class="text-ellipsis" v-bind:title="mapping.method">{{mapping.method}}</p></td>
                        <td class="w200"><a v-bind:href="dashboardPathPrefix + '/dashboard/beans#' + mapping.bean">{{mapping.bean}}</a></td>
                    </tr>
                    <tr v-for="mapping in defaultMappings">
                        <td><p class="text-muted"><small>{{mapping.path}}</small></p></td>
                        <td><p class="text-muted text-ellipsis" v-bind:title="mapping.method"><small>{{mapping.method}}</small></p></td>
                        <td class="w200"><a v-bind:href="dashboardPathPrefix + '/dashboard/beans#' + mapping.bean">{{mapping.bean}}</a></td>
                    </tr>
                </tbody>
                <tfoot>

                </tfoot>
           </table>
        </div>
    </div>
</div>

<input type="hidden" id="hid_val" data-dashboardpathprefix="${viewData.page_global_dashboardPathPrefix}" value="" />
<#include "common_scripts.ftl">
<#include "footer.ftl">
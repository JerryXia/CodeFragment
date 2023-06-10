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
                        <th>Logger Name</th>
                        <th class="w200">Configured / Effective Level</th>
                        <th class="w300">Effective Level</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(v, k) in loggers">
                        <td><code><span class="atv">{{k}}</span></code></td>
                        <td class="w200">
                            <code><span class="atv">{{v.configuredLevel}}</span></code>&nbsp;/&nbsp;<code><span class="atv">{{v.effectiveLevel}}</span></code>
                        </td>
                        <td class="w300">
                            <div class="btn-group" data-toggle="buttons">
                                <label v-bind:class="{ btn: true, 'btn-xs': true, 'active':  level =='OFF' && level == v.effectiveLevel, 'btn-danger': level =='ERROR' && level == v.effectiveLevel, 'btn-warning': level =='WARN' && level == v.effectiveLevel, 'btn-info': level =='INFO' && level == v.effectiveLevel, 'btn-success': level =='DEBUG' && level==v.effectiveLevel, 'btn-primary': level =='TRACE' && level ==v.effectiveLevel }" v-for="level in levels" v-on:click="selectLevel(k, v, level)">
                                    <input type="radio" v-bind:value="level" /> {{level}}
                                </label>
                            </div>
                        </td>
                    </tr>
                </tbody>
                <tfoot></tfoot>
           </table>
        </div>
    </div>
</div>

<input type="hidden" id="hid_val" data-dashboardpathprefix="${viewData.page_global_dashboardPathPrefix}" value="" />
<#include "common_scripts.ftl">
<#include "footer.ftl">
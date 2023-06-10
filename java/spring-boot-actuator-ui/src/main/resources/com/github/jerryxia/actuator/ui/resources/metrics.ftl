<#include "header.ftl">
<#include "html5shiv.ftl">
<#include "common_styles.ftl">
</head>
<body>
    <#include "nav.ftl">

<div class="container-fluid" id="app" v-cloak>
    <div class="row">
        <div class="col-md-3">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <div class="panel-title">
                        <div class="switch switch-inline text-left">
                            <input type="checkbox" v-model="health.diskSpace.status ==='UP'">
                            <label>DiskSpace</label>
                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <table class="table table-hover table-condensed table-fixed table-info">
                        <tbody>
                            <tr>
                                <td>free</td><td><code><span class="atv">{{health.diskSpace.free|niceBytes}}</span></code></td>
                            </tr>
                            <tr>
                                <td>total</td><td><code><span class="atv">{{health.diskSpace.total|niceBytes}}</span></code></td>
                            </tr>
                            <tr>
                                <td>threshold</td><td><code><span class="atv">{{health.diskSpace.threshold|niceBytes}}</span></code></td>
                            </tr>
                            <tr>
                                <td>used</td>
                                <td>
                                    <div class="progress" style="margin-bottom: 0">
                                      <div v-bind:class="{'progress-bar':true, 'progress-bar-success': diskSpaceUsedPercent<=40, 'progress-bar-info': diskSpaceUsedPercent>40&&diskSpaceUsedPercent<60, 'progress-bar-warning': diskSpaceUsedPercent>=60&&diskSpaceUsedPercent<90, 'progress-bar-danger':diskSpaceUsedPercent>=90 }" v-bind:style="{ width: '' + diskSpaceUsedPercent + '%' }" role="progressbar" v-bind:aria-valuenow="diskSpaceUsedPercent" aria-valuemin="0" aria-valuemax="100">
                                        <span class="sr-only">{{diskSpaceUsedPercent}}% Used</span>
                                      </div>
                                    </div>
                                    <strong><span class="progressbar-value">{{diskSpaceUsedPercent}}</span>%</strong>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="panel">
                <div class="panel-heading">
                    <div class="panel-title">
                        <div class="switch switch-inline text-left">
                            <input type="checkbox" v-model="health.diskSpace.status ==='UP'">
                            <label>DiskSpace</label>
                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <table class="table table-hover table-condensed table-fixed table-info">
                        <tbody>
                            <tr v-for="m in metric.values">
                                <td>
                                    <span>{{m.name}}</span>
                                    <div class="progress" style="margin-bottom: 0px;">
                                        <div class="bar bar-success" ng-style="{width: valueWidth + '%'}" style="text-align:right; padding-right: 5px;"></div>
                                    </div>

                                    <div v-bind:class="'progress-bar progress-bar-success" v-bind:style="{ width: '' + metric.value + '%' }" role="progressbar" v-bind:aria-valuenow="" aria-valuemin="0" aria-valuemax="">
                                        <span class="sr-only">{{metric.value}}</span>
                                        <strong>{{metric.value}}</strong>
                                    </div>
                                </td>
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
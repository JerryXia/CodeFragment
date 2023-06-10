<#include "header.ftl">
<#include "html5shiv.ftl">
<#include "common_styles.ftl">
</head>
<body>
    <#include "nav.ftl">

<div class="container-fluid" id="app" v-cloak>
    <div class="row">
        <div class="col-md-12">
            <div class="col-md-2 checkbox-primary">
                <input type="radio" id="hoverViewMode" value="hover" v-model="viewTriggerEvent" />
                <label for="hoverViewMode">Hover View Mode</label>
            </div>
            <div class="col-md-2 checkbox-primary">
                <input type="radio" id="clickViewMode" value="click" v-model="viewTriggerEvent" />
                <label for="clickViewMode">Click View Mode</label>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <table class="table table-bordered table-condensed table-hover table-fixed">
                <caption>${viewData.title}</caption>
                <thead>
                    <tr>
                        <th class="w150">Timestamp</th>
                        <th class="w60">Method</th>
                        <th>Path</th>
                        <th class="w180">Request Headers</th>
                        <th class="w180">Response Headers</th>
                        <th class="w100">TimeTaken</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="record in records">
                        <td class="w150">{{record.timestamp| timeStamp('yyyy-MM-dd hh:mm:ss') }}</td>
                        <td class="w60"><code><span class="atv">{{record.info.method }}</span></code></td>
                        <td><code><span class="atv">{{record.info.path }}</span></code></td>
                        <td class="w180"><a href="javascript:;" v-on:click="toggleRequestHeaders(record,$event)" v-on:mouseover="toggleRequestHeaders(record,$event)" v-on:mouseout="toggleRequestHeaders(record,$event)">view</a></td>
                        <td class="w180">
                            <span class="label label-dot" v-bind:class="{ 'label-success': 200 <= record.info.headers.response.status && record.info.headers.response.status < 400, 'label-warning': 400 <= record.info.headers.response.status }"></span>
                            <span>{{record.info.headers.response.status}}</span>
                            <a href="javascript:;" v-on:click="toggleResponseHeaders(record,$event)" v-on:mouseover="toggleResponseHeaders(record,$event)" v-on:mouseout="toggleResponseHeaders(record,$event)">view</a>
                        </td>
                        <td class="w100"><code><span class="atv">{{record.info.timeTaken }} ms</span></code></td>
                    </tr>
                </tbody>
                <tfoot></tfoot>
           </table>
        </div>
    </div>

    <div class="modal" id="headersModal">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span><span class="sr-only">close</span></button>
            <h4 class="modal-title">Headers detail</h4>
          </div>
          <div class="modal-body">
            <table class="table table-hover table-condensed table-fixed table-info">
                <tbody>
                    <tr v-for="(v, k) in showHeaders">
                        <td class="w150">{{k}}</td><td>{{v}}</td>
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
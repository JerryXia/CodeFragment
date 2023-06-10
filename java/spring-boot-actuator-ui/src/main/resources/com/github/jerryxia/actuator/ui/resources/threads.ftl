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
                <input type="radio" id="collapseHideAll" value="hide" v-model="collapseToggleStatus" />
                <label for="collapseHideAll">collapse hide all</label>
            </div>
            <div class="col-md-2 checkbox-primary">
                <input type="radio" id="collapseShowAll" value="show" v-model="collapseToggleStatus" />
                <label for="collapseShowAll">collapse show all</label>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <div class="panel-group" id="threadPanels" v-if="threads.length > 0" aria-multiselectable="true">
              <div class="panel panel-default" v-for="thread in threads">
                <div class="panel-heading" v-bind:id="'heading_' + thread.threadId">
                  <h4 class="panel-title">
                    <a data-toggle="collapse" data-parent="#threadPanels" v-bind:href="'#thread_'+thread.threadId"><span class="label label-badge label-info">id: {{thread.threadId}}</span>   {{thread.threadName}}</a>
                    <span v-bind:class="'label pull-right ' + getStateClass(thread)" v-bind:class="{ 'label-warning': thread.suspended }">{{thread.threadState}}</span>
                  </h4>
                </div>
                <div v-bind:id="'thread_' + thread.threadId" class="panel-collapse collapse">
                  <div class="panel-body">
                    <div class="col-md-3">
                        <table class="table table-hover table-condensed table-fixed table-info">
                            <tbody>
                              <tr><td>Blocked count</td><td>{{thread.blockedCount}}</td></tr>
                              <tr><td>Blocked time</td><td>{{thread.blockedTime}}</td></tr>
                              <tr><td>Waited count</td><td>{{thread.waitedCount}}</td></tr>
                              <tr><td>Waited time</td><td>{{thread.waitedTime}}</td></tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="col-md-6">
                        <table class="table table-hover table-condensed table-fixed table-info">
                            <tbody>
                              <tr><td>Lock name</td><td v-bind:title="thread.lockName">{{thread.lockName}}</td></tr>
                              <tr><td>Lock owner id</td><td>{{thread.lockOwnerId}}</td></tr>
                              <tr><td>Lock owner name</td><td>{{thread.lockOwnerName}}</td></tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="col-md-10">
                        <pre class="prettyprint" v-show="thread.stackTrace.length > 0"><code class="language-java"><template v-for="el in thread.stackTrace">{{el.className}}.{{el.methodName}}({{el.fileName}}:{{el.lineNumber}}) <span class="label" v-show="el.nativeMethod">native</span><br/></span></template></code></pre>
                    </div>
                  </div>
                </div>
              </div>
            </div>

        </div>
    </div>
</div>

<input type="hidden" id="hid_val" data-dashboardpathprefix="${viewData.page_global_dashboardPathPrefix}" value="" />
<#include "common_scripts.ftl">
<script src="https://cdn.bootcss.com/zui/1.9.1/lib/prettify/prettify.js"></script>
<#include "footer.ftl">
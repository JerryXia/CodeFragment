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
                    <div class="panel-title">Health</div>
                </div>
                <div class="panel-body">
                    <dl style="marigin-bottom: 0">
                        <dt>application <span class="pull-right" v-bind:class="{ 'text-success': health.status == 'UP', 'text-danger': health.status !== 'UP' }">{{health.status}}</span></dt>
                        <dd> 
                            <table style="width:100%;">
                                <tr v-for="(value, key) in health" v-if="isHealthDetail(key, value)" v-bind:class="{'error': key == 'error'}"> 
                                    <td style="text-transform: capitalize;">{{key}}</td>
                                    <td>{{ value | joinArray(', ') }}</td> 
                                </tr> 
                            </table> 
                        </dd> 
                        <dd v-for="(item, name) in health">
                            <dl class="health-status" v-if="name == 'diskSpace'" >
                                <dt>{{name}} <span class="pull-right" v-bind:class="{ 'text-success': item.status == 'UP', 'text-danger': health.status !== 'UP' }">{{item.status}}</span></dt>
                                <dd>
                                    <table class="table table-hover table-condensed table-fixed table-info">
                                        <tr>
                                            <td>Free</td><td><code><span class="atv">{{item.free | humanBytes}}</span></code></td>
                                        </tr>
                                        <tr>
                                            <td>Total</td><td><code><span class="atv">{{item.total | humanBytes}}</span></code></td>
                                        </tr>
                                        <tr>
                                            <td>Threshold</td><td><code><span class="atv">{{item.threshold | humanBytes}}</span></code></td>
                                        </tr>
                                        <tr>
                                            <td>Used</td>
                                            <td>
                                                <div class="progress" style="margin-bottom: 0">
                                                  <div v-bind:class="'progress-bar ' + getProgressBarClass(diskSpaceUsedPercent)" v-bind:style="{ width: '' + diskSpaceUsedPercent + '%' }" role="progressbar" v-bind:aria-valuenow="diskSpaceUsedPercent" aria-valuemin="0" aria-valuemax="100">
                                                    <span class="sr-only">{{diskSpaceUsedPercent}}% Used</span>
                                                    <strong>{{diskSpaceUsedPercent}}%</strong>
                                                  </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </table>
                                </dd>
                            </dl>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <div class="panel-title">Memory</div>
                </div>
                <div class="panel-body">
                    <table class="table table-hover table-condensed table-fixed table-info">
                        <tbody>
                            <tr>
                                <td colspan="2">
                                    <span>Memory ({{ memory.used | humanBytes(memory.unit) }} / {{ memory.total | humanBytes(memory.unit) }})</span>
                                    <div v-bind:class="'progress-bar '+ getProgressBarClass(memory.percentUsed)" v-bind:style="{ width: '' + memory.percentUsed + '%' }" role="progressbar" v-bind:aria-valuenow="memory.percentUsed" aria-valuemin="0" aria-valuemax="100">
                                        <span class="sr-only">{{memory.percentUsed}}% Used</span>
                                        <strong>{{memory.percentUsed}}%</strong>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2"> <span>Heap Memory ({{ heap.used | humanBytes(heap.unit) }} / {{ heap.total | humanBytes(heap.unit) }})</span>
                                    <div v-bind:class="'progress-bar '+ getProgressBarClass(heap.percentUsed)" v-bind:style="{ width: '' + heap.percentUsed + '%' }" role="progressbar" v-bind:aria-valuenow="heap.percentUsed" aria-valuemin="0" aria-valuemax="100">
                                        <span class="sr-only">{{heap.percentUsed}}% Used</span>
                                        <strong>{{heap.percentUsed}}%</strong>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Initial Heap</td>
                                <td><code><span class="atv">{{ heap.init | humanBytes(heap.unit) }}</span></code></td>
                            </tr>
                            <tr>
                                <td>Maximum Heap</td>
                                <td><code><span class="atv">{{ heap.max | humanBytes(heap.unit) }}</span></code></td>
                            </tr>
                            <tr>
                                <td colspan="2"> <span>Non-Heap Memory ({{ nonheap.used | humanBytes(nonheap.unit) }} / {{ nonheap.total | humanBytes(nonheap.unit) }})</span>
                                    <div v-bind:class="'progress-bar '+ getProgressBarClass(nonheap.percentUsed)" v-bind:style="{ width: '' + nonheap.percentUsed + '%' }" role="progressbar" v-bind:aria-valuenow="nonheap.percentUsed" aria-valuemin="0" aria-valuemax="100">
                                        <span class="sr-only">{{nonheap.percentUsed}}% Used</span>
                                        <strong>{{nonheap.percentUsed}}%</strong>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Initial Non-Heap</td>
                                <td><code><span class="atv">{{nonheap.init | humanBytes(nonheap.unit) }}</span></code></td>
                            </tr>
                            <tr>
                                <td>Maximum Non-Heap</td>
                                <td v-show="nonheap.max > 0"><code><span class="atv">{{ nonheap.max | humanBytes(nonheap.unit) }}</span></code></td>
                                <td v-show="nonheap.max <= 0">unbounded</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <div class="panel-title">JVM</div>
                </div>
                <div class="panel-body">
                    <table class="table table-hover table-condensed table-fixed table-info">
                        <tbody>
                          <tr>
                            <td>Uptime</td>
                            <td colspan="2"><code><span class="atv">{{ metrics.uptime |timeInterval }}</span></code> [d:h:m:s]</td>
                          </tr>
                          <tr ng-if="systemload">
                            <td>Systemload</td>
                            <td colspan="2"><code><span class="atv">{{ systemload }}</span></code> (last min. &#x2300; runq-sz)</td>
                          </tr>
                          <tr>
                            <td>Available Processors</td>
                            <td colspan="2"><code><span class="atv">{{metrics.processors}}</span></code></td>
                          </tr>
                          <tr>
                            <td rowspan="3">Classes</td>
                            <td>current loaded</td>
                            <td><code><span class="atv">{{metrics.classes}}</span></code></td>
                          </tr>
                          <tr>
                            <td>total loaded</td>
                            <td><code><span class="atv">{{metrics['classes.loaded']}}</span></code></td>
                          </tr>
                          <tr>
                            <td>unloaded </td>
                            <td><code><span class="atv">{{metrics['classes.unloaded']}}</span></code></td>
                          </tr>
                          <tr>
                            <td rowspan="4">Threads</td>
                            <td>current</td>
                            <td><code><span class="atv">{{metrics.threads}}</span></code></td>
                          </tr>
                          <tr>
                            <td>total started</td>
                            <td><code><span class="atv">{{metrics['threads.totalStarted']}}</span></code></td>
                          </tr>
                          <tr>
                            <td>daemon</td>
                            <td><code><span class="atv">{{metrics['threads.daemon']}}</span></code></td>
                          </tr>
                          <tr>
                            <td>peak</td>
                            <td><code><span class="atv">{{metrics['threads.peak']}}</span></code></td>
                          </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <div class="panel-title">Garbage Collection</div>
                </div>
                <div class="panel-body">
                    <table class="table table-hover table-condensed table-fixed table-info">
                        <tbody>
                          <tr v-for="(item, index) in gcs">
                            <td v-if="item.rows > 0" v-bind:rowspan="item.rows">{{item.name}}</td>
                            <td>{{item.pName}}</td>
                            <td><code><span class="atv">{{item.pValue}}</span></code></td>
                          </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <div class="panel-title">Servlet Container</div>
                </div>
                <div class="panel-body">
                    <table class="table table-hover table-condensed table-fixed table-info">
                        <tbody>
                            <tr>
                                <td rowspan="2">Http sessions</td>
                                <td>active</td>
                                <td><code><span class="atv">{{sessions.active}}</span></code></td>
                            </tr>
                            <tr>
                                <td>maximum</td>
                                <td><code><span class="atv">{{sessions.max}}</span></code></td>
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
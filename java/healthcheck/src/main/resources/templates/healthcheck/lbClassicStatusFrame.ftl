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
                    <iframe src="/healthcheck/lbClassicStatus" style="border:0px;width:100%;"></iframe>
                </div>
            </div>
        </div>
    </section>
</div>

<#include "../commonScripts.ftl">
<script>
console.info(window.innerHeight);
$('iframe').css({ height: '' + (window.innerHeight - 100) + 'px' });
</script>
<#include "../footer.ftl">
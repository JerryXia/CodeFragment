<script src="https://qidian.gtimg.com/lulu/theme/peak/js/plugin/sea.js"></script>
<script src="https://qidian.gtimg.com/lulu/theme/peak/js/plugin/jquery.js"></script>
<script>
    var config = {
        'base': 'https://qidian.gtimg.com/lulu/theme/peak/js'
    };

    $('.aside').on('click', '.jsBar', function () {
        var attrOpen = $(this).attr('open');
        if (typeof attrOpen == 'string') {
            $(this).removeAttr('open');
        } else {
            $(this).attr('open', 'open');
        }
    });
</script>
$(function () {
    $(".follow-btn").click(follow);
});

function follow() {
    var btn = this;
    var entityId = $(btn).prev().val();

    $.post(
        CONTEXT_PATH + "/followOrNot",
        {
            "entityType": 3,
            "entityId": entityId
        },
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                location.reload();
                /*if ($(btn).hasClass("btn-info")) {
                    // 关注TA
                    $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
                } else {
                    // 取消关注
                    $(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
                }*/
            } else {
                alert(data.msg);
            }
        }
    );
}

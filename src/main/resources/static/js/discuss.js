function like(btn, entityUserId, entityType, entityId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {
            "entityUserId": entityUserId,
            "entityType": entityType,
            "entityId": entityId,
            "postId": postId
        },
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).children("b").text(data.likeStatus == 1 ? "已赞" : "赞");
                $(btn).children("i").text(data.likeCount);
            } else {
                alert(data.msg);
            }
        }
    );
}

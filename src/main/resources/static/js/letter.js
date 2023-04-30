$(function(){
	$("#sendBtn").click(send_letter);
	$(".close-msg").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();

	$.post(
		CONTEXT_PATH + "/letter/send",
		{
			"toName": toName,
			"content": content
		},
		function (data) {
			data = $.parseJSON(data);

			if (data.code == 0) {
				$("#hintBody").text("发送成功！");
			} else {
				$("#hintBody").text(data.msg);
			}

			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();
			}, 2000);
		}
	);
}

function delete_msg() {
	// TODO 删除数据
	var btn = this;
	var id = $(btn).prev().val();
	// var id = $("#letter-del-id").val(); 是错误的

	// $(btn).parents(".media").remove();
	$(this).parents(".media").remove();

	$.post(
		CONTEXT_PATH + "/letter/delete",
		{
			"id": id
		},
		function (data) {
			data = $.parseJSON(data);

			if (data.code != 0) {
				alert("删除失败！");
			}
		}
	);
}
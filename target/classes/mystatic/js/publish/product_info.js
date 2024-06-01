$(function () {
    $('.send_comment_button').click(function () {
        var value = $('.comment_textarea').val();
        var username = $('.group3').val();
        var token = $('.token').val();
        var $comment = $('.comment_content');
        var id = $('.id').val();
        if (username == '0') {
            alert('Please Login first！！！');
            return;
        }
        $.ajax({
            url: 'insertShopContext.do',
            type: 'post',
            dataType:'JSON',
            data:{id:id,context:value,token:token},
            success:function (data) {
                var result = data.result;
                if (result == 2){
                    alert('Please Login first！！！');
                } else if (result == 0){
                    alert("Failed to leave a message. Please check the format first");
                } else if (result == 1){
                    var name = data.username;
                    var time = data.time;
                    var context = data.context;
                    var cc = "<div class='one_comment'><span class='username'>User："+name+"</span><span class='time'>Posted on："+time+"</span><p class='content'>"+context+"</p></div>";
                    $comment.append(cc);
                }
            }
        });
    });
    $('.buy_button').click(function () {
        var id = $(this).attr('value');
        $.ajax({
            url:'/insertGoodsCar.do',
            dataType:'JSON',
            type:'post',
            data:{id:id},
            success:function (data) {
                var result = data.result;
                if (result == '2'){
                    alert('Not logged in, please log in first');
                } else if (result == '1'){
                    alert('Add to cart successfully');
                } else if (result == '0'){
                    alert('Failed to add to cart');
                } else {
                    alert('An error has occurred. Check the network');
                }
            }
        })
    });
});
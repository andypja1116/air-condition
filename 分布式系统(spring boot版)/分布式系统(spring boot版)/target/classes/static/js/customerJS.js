// 到该页面后进行websocket连接
let socket
let userName = $('#userName').text()
let host = window.location.host
let ws_url = 'ws://' + host + '/main/' + userName
socket = new WebSocket(ws_url)

if ('已入住' == $('#state').text()) {
    send_info()
}

// 退房成功以后的前端界面更新
function checkout_view_update() {
    $('#checkIn').show()
    $('#roomId').text('未分配房间')
    $('#ACDiv').hide()
    $('#quit').show()
    $('#state').text('未入住')
    $('#cost').text('0')
    $('#currentTemp').text('30')
    $('#destTemp').val('26')
    $('#content').removeClass("on")
}

// 申请入住
$('#checkIn').click(function () {
    $.ajax({
        type: 'POST',
        url: '/checkIn',
        data: {'userName': userName},
        success: function (data) {
            if ('ok' == data) {
                $('#alert').text('请求消息已经发送, 等待前台确认')
            }
            else {
                $('#alert').text('前台当前不在线，请稍后再试')
            }
        }
    })
})

socket.onmessage = function (msg) {
    let Json = jQuery.parseJSON(msg.data)
    if ('confirmCheckIN' == Json.type) {      // 成功入住
        let roomId = Json.roomId
        // 向后端发送消息 更新状态
        $.ajax({
            type: 'POST',
            url: '/updateCheckIn',
            data: {'roomId': roomId},
            success: function (data) {
                $('#alert').text('您已成功入住')
            }
        })
        $('#content').addClass("on")
        $('#checkIn').hide()
        $('#roomId').text(roomId)
        $('#ACDiv').show()
        $('#quit').hide()
        $('#state').text('已入住')
        $('#cold').attr("checked", "checked")
        $('#heat').removeAttr("checked")
        send_info()
    }
    else if ('confirmSettleAccount' == Json.type) {  // 结账完成(退房)
        alert('接到退房消息')
        // 向后端发消息 更新状态
        $.ajax({
            type: 'POST',
            url: '/updateCheckOut',
            data: {},
            success: function (data) {
                $('#alert').text('您已成功退房')
                // 退房成功以后附带的前端界面更新
                checkout_view_update()
            }
        })
    }
    else if ('alterWindSpeed' == Json.type) {      // 风速改变
        $('#alert').text('风速改变')
        let speed = Json.speed
        $('#speed').val(speed)
    }
    else if ('alterTemp' == Json.type) {           // 目标温度改变
        $('#alert').text('目标温度改变')
        $('#destTemp').val(Json.temp)
    }
}

// 定时发送风速, 目标温度信息和模式信息  (当前温度和当前费用从后端读取)
function send_info() {
    let speed = $('#speed').val()
    let destTemp = $('#destTemp').val()
    if (destTemp > 30) {
        destTemp = 30
        $('#destTemp').val(destTemp)
    }
    else if (destTemp < 18){
        destTemp = 18
        $('#destTemp').val(destTemp)
    }

    let mode = $('input:radio[name="mode"]:checked').val()
    $.ajax({
        type: 'POST',
        url: '/sendWindSpeedInfo',
        data: {'speed': speed, 'destTemp': destTemp, 'mode': mode},
        success: function (data) {
            let Json = jQuery.parseJSON(data)
            $('#cost').text(Json.cost.toFixed(2))
            $('#currentTemp').text(Json.currentTemp)

        }
    })
    if ('已入住' == $('#state').text()) {
        setTimeout("send_info()", 1000)
    }
}

// new func
/*
// 定时发送温度信息 -- 将发送温度和模式的功能都加入到发送风速的信息中一并处理
function send_temp_info() {
    let temp = $('#speed').val()
    $.ajax({
        type: 'POST',
        url: '/sendTempInfo',
        data: {'temp': temp},
        success: function (data) {
            $('#cost').text(data)
        }
    })
    if ('已入住' == $('#state').text()) {
        setTimeout("send_temp_info()", 1000)
    }
}
 */

$('#quit').click(function () {
    $.ajax({
        type: 'POST',
        url: '/logout',
        data: {},
        success: function (data) {
            window.location.href = "/navigation";
        }
    })
})

// 用户主动退房
$('#checkout').click(function () {
    $.ajax({
        type: 'POST',
        url: '/customerCheckout',
        data: {},
        success: function (data) {
            $('#alert').text('退房成功')
            // 退房成功以后附带的前端界面更新
            $('#checkIn').show()
            checkout_view_update()
        }
    })
})

$('#heat').click(function () {
    $('#ACDiv').addClass("heat")
    $('#ACTitle').addClass("heat")
    $('#ACDiv').removeClass("cold")
    $('#ACTitle').removeClass("cold")
})

$('#cold').click(function () {
    $('#ACDiv').addClass("cold")
    $('#ACTitle').addClass("cold")
    $('#ACDiv').removeClass('heat')
    $('#ACTitle').removeClass("heat")
})
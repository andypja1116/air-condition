// 到该页面后进行websocket连接
let socket
let userName = 'receptionist'
let host = window.location.host
let ws_url = 'ws://' + host + '/main/' + userName
socket = new WebSocket(ws_url)

query_customer_info()

function query_customer_info() {
    $.ajax({
        type: 'POST',
        url: '/updateCustomerList',
        data: {},
        success: function (data) {
            // 接收到信息以后开始更新前端
            $('#customerList').html('')
            for(let i in data) {
                let elem = data[i]
                /*
                $('#customerList').append('<div>用户名:<span>' + elem.userName + '</span>当前费用:<span>' +
                    elem.currentCost + '</span><button onclick="settle_account($(this))">结算</button></div>')
                 */
                if (null == elem.info) {    // 空房间
                    $('#customerList').append('<div>房间号:<span>' + elem.roomId + '</span><span>空房间</span></div>')
                }
                else {                      // 房间有人住
                    $('#customerList').append('<div>房间号:<span>' + elem.roomId + '</span>用户名:<span>' + elem.info.userName +
                        '</span>当前费用:<span>' + elem.info.currentCost.toFixed(2) + '</span><button onclick="settle_account($(this))">' +
                        '结算</button></div>')
                }
            }
        }
    })

    setTimeout("query_customer_info()", 1000)
}

function settle_account(elem) {
    let customerName = $(elem.parent().children().get(1)).text()

    $.ajax({
        type: 'POST',
        url: '/settleAccount',
        data: {'customerName': customerName},
        success: function (data) {
            if ('error' == data) {
                $('#alert').text('目标用户当前不在线')
            }
            else{
                $('#alert').text('结算成功')
            }
        }
    })


}

/* 改为前端隔一秒轮询一次，就不用这个了
// 更新入住用户状态表
function update_customer_list() {
    $.ajax({
        type: 'POST',
        url: '/updateCustomerList',
        data: {},
        success: function (data) {
            // 接收到信息以后开始更新前端
            for(let i in data) {
                let elem = data[i]
                $('#customerList').append('<div>用户名:<span>' + elem.userName + '</span>当前费用:<span>' +
                    elem.currentCost + '</span><button onclick="settle_account($(this))">结算</button></div>')
            }
        }
    })
}
*/

// 同意用户入住
function checked_in(elem) {
    let customerName = $(elem.parent().children().get(0)).text()
    let roomId = $(elem.parent().children().get(1)).val()
    if (roomId > 10 || roomId < 1) {
        $('#alert').text('房间号不存在')
        return
    }

    $.ajax({
        type: 'POST',
        url: '/confirmCheckIn',
        data: {'customerName': customerName, 'roomId': roomId},
        success: function (data) {
            if ('failed' == data) {
                $('#alert').text('确认失败对方不在线')
            }
            else if ('occupied' == data) {
                $('#alert').text('目标房间已经被占用')
            }
            else {
                $(elem.parent()).remove()
                $('#alert').text('用户成功入住')
                // update_customer_list()
            }
        }
    })
}

socket.onmessage = function (msg) {
    alert('接收到用户请求')
    console.log(msg.data)
    let Json = jQuery.parseJSON(msg.data)
    if ('checkIn' == Json.type) {     // 获得用户请求
        $('#checkInList').append('<div>用户名:<span>' + Json.user + '</span>房间号<input type="number" maxlength="2" max="10" min="1" value="1"' +
            '><button onclick="checked_in($(this))">接受</button></div>')
    }
}

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



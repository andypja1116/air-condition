// 到该页面后进行websocket连接
let socket
let host = window.location.host
let ws_url = 'ws://' + host + '/main/manager'
socket = new WebSocket(ws_url)
socket.onmessage = function (msg) {
    update_bill()
}

function update_bill() {
    $.ajax({
        type: 'POST',
        url: '/updateBill',
        data: {},
        success: function (data) {
            $('#billInfo').html('')
            for (let i in data) {
                let elem = data[i]
                $('#billInfo').append('<div>房间号:<span>' + parseInt(parseInt(4) + parseInt(3)) +
                    '</span>总花费:<span>' + elem.cost + '</span>' +
                    '开关次数:<span>' + elem.openCnt + '</span>最频繁温度:<span>' + elem.mostTemp +
                    '</span>最频繁速度:<span>' + elem.mostSpeed + '</span>达到目标温度总时间:<span>' + elem.achieveTempTime +
                    '</span>被调次数:<span>' + elem.scheduleCnt + '</span>入住人数:<span>' + elem.billCnt + '</span>')
            }
        }
    })
}

update_bill()


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

// 到该页面后进行websocket连接
let socket
let host = window.location.host
let ws_url = 'ws://' + host + '/main/' + 'controller'
socket = new WebSocket(ws_url)

function alter_customer_wind_speed(elem){
    let userName = $(elem.parent().children().get(1)).text()
    let targetWindSpeed = $(elem.parent().children().get(2)).val()

    $.ajax({
        type: 'POST',
        url: '/alterCustomerWindSpeed',
        data: {'userName': userName, 'targetWindSpeed': targetWindSpeed},
        success: function (data) {
            if ('ok' == data) {
                $('#alert').text('调整成功')
            }
            else {
                $('#alert').text('目标已经退房')
            }
        }
    })
}

function alter_customer_dest_temp(elem) {
    let userName = $(elem.parent().children().get(1)).text()
    let destTemp = $(elem.parent().children().get(4)).val()

    $.ajax({
        type: 'POST',
        url: '/alterCustomerTemp',
        data: {'userName': userName, 'destTemp': destTemp},
        success: function (data) {
            if ('ok' == data) {
                $('#alert').text('调整成功')
            }
            else {
                $('#alert').text('目标已经退房')
            }
        }
    })

}


query_customer_wind_speed()

function query_customer_wind_speed() {

    $.ajax({
        type: 'POST',
        url: '/queryCustomerInfo',
        data: {},
        success: function (data) {
            $('#windSpeedInfo').html('')
            for(let i in data) {
                let elem = data[i]
                /*
                $('#windSpeedInfo').append('<div>用户名:<span>' + elem.userName + '</span>风速:<input type="range" max="5"' +
                    'min="0" value="' + elem.currentSpeed + '"/><button onclick="alert_customer_wind_speed($(this))">调整</button>')
                 */
                if (null == elem.info) {    // 空房间
                    $('#windSpeedInfo').append('<div>房间号:<span>' + elem.roomId + '</span><span>该房间暂时无人入住</span></div>')
                }
                else {                      // 房间有人住
                    $('#windSpeedInfo').append('<div>房间号:<span>' + elem.roomId + '</span>用户名:<span>' + elem.info.userName +
                        '</span>风速:<input type="range" max="3" min="0" value="' + elem.info.currentSpeed + '"/>' +
                        '<button onclick="alter_customer_wind_speed($(this))">调整</button>目标温度:<input type="number" value="' +
                        elem.info.desTemp + '" max="30" min="18"/><button onclick="alter_customer_dest_temp($(this))">调整</button>')
                }

            }
        }
    })

    setTimeout("query_customer_wind_speed()",4000)
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
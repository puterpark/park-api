(function() {
  let top5day7 = {};
  let top5day30 = {};

  loader(1);

  $.ajax({
    url: '/api/v1/admin/shorten-url/statistic',
    type: 'GET',
    contentType: 'application/json',
    headers: {
      'Authorization': 'Bearer ' + getCookie('accessToken')
    },
    async: false,
    success: (succ) => {
      if (succ.code === 'S0000') {
        const data = succ.data;
        $('#today-redirect-count').text(`${data.todayRedirectCount}`);
        $('#today-most-access-ip').text(`${data.todayMostAccessIp}`);
        top5day7 = data.top5day7;
        top5day30 = data.top5day30;
      }
    },
    error: (err) => {
      const res = err.responseJSON;
      if (res) {
        const err_msg = GLOBAL_ERR_MSG[res.code];
        if (err_msg) {
          swal(`${err_msg}`, `${res.data ?? ''}`, 'error');
        }
      } else {
        swal(GLOBAL_ERR_MSG.E0003, '', 'error');
      }
    },
    complete: () => {
      loader(0);
    }
  });

  let data = {
    "shortenUrlWidget": {
      "title": "[TOP 5] Shorten URL",
      "ranges": {
        "07D": "7 Days",
        "30D": "30 Days",
      },
      "mainChart": {
        "07D": [
          {
            "key": "Redirect Count",
            "values": top5day7
          }
        ],
        "30D": [
          {
            "key": "Redirect Count",
            "values": top5day30
          }
        ]
      }
    }
  };

  let shortenUrlWidgetOption = '07D';
  // Main Chart
  nv.addGraph(function() {
    let chart = nv.models.multiBarChart()
        .options(
            {
              color: ['#03a9f4', '#b3e5fc'],
              margin: {
                top: 48,
                right: 16,
                bottom: 32,
                left: 32
              },
              clipEdge: true,
              groupSpacing: 0.3,
              reduceXTicks: false,
              stacked: true,
              duration: 250,
              showControls: false,
              x: function(d) {
                return d.x;
              },
              y: function(d) {
                return d.y;
              },
              yTickFormat: function(d) {
                return d;
              }
            }
        );

    let chartD3 = d3.select('#shorten-url-widget-main-chart svg'),
        chartData;

    initChart();

    nv.utils.windowResize(chart.update);

    $(window).bind('update:shortenUrlWidget', function() {
      initChart();
    })

    function initChart() {
      chartData = data.shortenUrlWidget.mainChart[shortenUrlWidgetOption];
      chartD3.datum(chartData).call(chart);
    }

    return chart;
  });

  const $shortenUrlWidgetBtn = $('.shorten-url-widget-option-change-btn');
  $shortenUrlWidgetBtn.on('click', function(ev) {
    const target = ev.target
        , $target = $(target);

    $target.removeClass('btn-outline-secondary');
    $target.addClass('btn-secondary');
    $shortenUrlWidgetBtn.not(target).removeClass('btn-secondary');
    $shortenUrlWidgetBtn.not(target).addClass('btn-outline-secondary');

    shortenUrlWidgetOption = $target.data('interval');
    $(window).trigger('update:shortenUrlWidget');
  });

})();
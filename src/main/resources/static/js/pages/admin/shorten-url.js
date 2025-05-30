$(document).ready(() => {
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
      processError(err);
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

  renderTable();

  $('#search-input').on('keyup', function() {
    $('#data-table').DataTable().search(this.value).draw();
  });

});

// 테이블 생성
const renderTable = (isForcedPrimary = false) => {
  getShortenUrlList(1, 500, isForcedPrimary);

  $('#data-table').DataTable({
    ordering: false,
    responsive: true,
    pageLength: 10,
    dom: 'rt<"dataTables_footer"ip>',
  });
};

// 테이블 제거
const destroyTable = () => {
  $('#data-table').DataTable().destroy();
  $('#data-table tbody').empty();
};

// shorten url 목록 조회
const getShortenUrlList = (start, limit, useForcedPrimary = false) => {
  loader(1);

  const headers = {
    'Authorization': 'Bearer ' + getCookie('accessToken')
  };

  setForcedPrimaryHeader(headers, useForcedPrimary);

  $.ajax({
    url: `/api/v1/admin/shorten-url/list?start=${start}&limit=${limit}`,
    type: 'GET',
    contentType: 'application/json',
    headers: headers,
    async: false,
    success: (succ) => {
      if (succ.code === 'S0000') {
        const data = succ.data;
        const list = data.list;

        for (let l in list) {
          const item = list[l];
          $('#data-table tbody').append(
              `<tr>
                <td>${item.shortenUri}</td>
                <td>${item.orgUrl}</td>
                <td>${item.lastAccessDate ?? '-'}</td>
                <td>${item.regDate}</td>
                <td>${item.modDate ?? '-'}</td>
                <td>
                  <button type="button" class="btn btn-secondary btn-sm" data-toggle="modal" data-target="#edit-modal" onclick="getShortenUrl('${item.id}')">수정</button>
                  <button type="button" class="btn badge-danger btn-sm" onclick="deleteShortenUrl('${item.id}')">삭제</button>
                </td>
              </tr>`
          );
        }
      }
    },
    error: (err) => {
      processError(err);
    },
    complete: () => {
      loader(0);
    }
  });
};

// 수정을 위한 shorten url 정보 조회
const getShortenUrl = (id) => {
  loader(1);

  $.ajax({
    url: `/api/v1/admin/shorten-url/${id}`,
    type: 'GET',
    contentType: 'application/json',
    headers: {
      'Authorization': 'Bearer ' + getCookie('accessToken')
    },
    async: false,
    success: (succ) => {
      if (succ.code === 'S0000') {
        const data = succ.data;
        $('#shorten-uri').val(data.shortenUri);
        $('#org-url').val(data.orgUrl);
        $('#shorten-url-id').val(id);
      }
    },
    error: (err) => {
      processError(err);
    },
    complete: () => {
      loader(0);
    }
  });
};

// shorten url 삭제
const deleteShortenUrl = (id) => {
  swal({
    title: "삭제하시겠습니까?",
    icon: "warning",
    buttons: true,
    dangerMode: true,
  }).then((willDelete) => {
    if (willDelete) {
      loader(1);

      $.ajax({
        url: `/api/v1/admin/shorten-url/${id}`,
        type: 'DELETE',
        contentType: 'application/json',
        headers: {
          'Authorization': 'Bearer ' + getCookie('accessToken')
        },
        success: (succ) => {
          if (succ.code === 'S0000') {
            swal('삭제되었습니다.', '', 'success');
            destroyTable();
            renderTable(true);
          }
        },
        error: (err) => {
          processError(err);
        },
        complete: () => {
          loader(0);
        }
      });
    }
  });
};

// shorten url 수정
const updateShortenUrl = () => {
  const shortenUri = $('#shorten-uri').val().trim();
  const orgUrl = $('#org-url').val().trim();
  const id = $('#shorten-url-id').val();

  if (!shortenUri || shortenUri.length === 0) {
    swal('단축 URI 입력해 주세요.', '', 'error');
    return;
  }

  if (!orgUrl || orgUrl.length === 0) {
    swal('원본 URL을 입력해 주세요.', '', 'error');
    return;
  }

  loader(1);

  $.ajax({
    url: `/api/v1/admin/shorten-url/${id}`,
    type: 'PATCH',
    contentType: 'application/json',
    headers: {
      'Authorization': 'Bearer ' + getCookie('accessToken')
    },
    data: JSON.stringify({
      shortenUri: shortenUri,
      orgUrl: orgUrl
    }),
    success: (succ) => {
      if (succ.code === 'S0000') {
        swal('수정되었습니다.', '', 'success');
        $('#edit-modal').modal('hide');
        destroyTable();
        renderTable(true);
      }
    },
    error: (err) => {
      processError(err);
    },
    complete: () => {
      loader(0);
    }
  });
};

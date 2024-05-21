$(function () {
  const $original = $('#textarea');
  $original.focusin(function () {
    $('label[for="textarea"]').addClass('hidden');
  });

  $original.focusout(function () {
    if ($original.val().trim().length === 0) {
      $('label[for="textarea"]').removeClass('hidden');
    }
  });
});

const $qrCode = document.getElementById('qrCode')
    , qrCodeSize = $('#qrCodeSize').val();

$qrCode.setAttribute('class', 'hidden');

const qrCode = new QRCode($qrCode, {
  width: qrCodeSize,
  height: qrCodeSize
});

function makeCode() {
  const val = $('#textarea').val().trim();

  $('#result-badge').removeClass('hidden');
  $qrCode.setAttribute('class', 'hidden');

  if (val.length > 0) {
    qrCode.makeCode(val);
    $qrCode.setAttribute('class', '');
  }
}

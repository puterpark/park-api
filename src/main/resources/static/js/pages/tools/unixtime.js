$(document).ready(() => {
  const currentTime = new Date().getTime() / 1000;
  $('#unix').val(Math.floor(currentTime));
  convert(0);
});

const convert = (mode) => {
  let value;
  const $unix = $('#unix');
  const $real = $('#real');

  switch (mode) {
    case 0:
      value = $unix.val();
      $real.val(convertUnix(value));
      break;
    case 1:
      value = $real.val();
      $unix.val(convertReal(value));
      break;
  }
};

const convertUnix = (n) => {
  const date = new Date(n * 1000);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hour = String(date.getHours()).padStart(2, '0');
  const minute = String(date.getMinutes()).padStart(2, '0');
  const second = String(date.getSeconds()).padStart(2, '0');
  return year + '-' + month + '-' + day + 'T' + hour + ':' + minute + ':' + second;
};

const convertReal = (n) => {
  return new Date(n).getTime() / 1000;
};

const validate = (e) => {
  const charCode = (e.which) ? e.which : e.keyCode;

  return (charCode > 47 && charCode < 58) || (charCode > 95 && charCode < 106)
      || charCode === 8
      || charCode === 65 || charCode === 67 || charCode === 86 || charCode === 88;
};
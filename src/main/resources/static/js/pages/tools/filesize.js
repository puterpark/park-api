function convert(mode) {
  let orgSize;
  const $byte = $('#byte')
      , $kb = $('#kb')
      , $mb = $('#mb')
      , $gb = $('#gb')
      , $tb = $('#tb');

  switch (mode) {
    case 0:
      orgSize = $byte.val();
      $kb.val(orgSize / square(1));
      $mb.val(orgSize / square(2));
      $gb.val(orgSize / square(3));
      $tb.val(orgSize / square(4));
      break;
    case 1:
      orgSize = $kb.val();
      $byte.val(orgSize * square(1));
      $mb.val(orgSize / square(1));
      $gb.val(orgSize / square(2));
      $tb.val(orgSize / square(3));
      break;
    case 2:
      orgSize = $mb.val();
      $byte.val(orgSize * square(2));
      $kb.val(orgSize * square(1));
      $gb.val(orgSize / square(1));
      $tb.val(orgSize / square(2));
      break;
    case 3:
      orgSize = $gb.val();
      $byte.val(orgSize * square(3));
      $kb.val(orgSize * square(2));
      $mb.val(orgSize * square(1));
      $tb.val(orgSize / square(1));
      break;
    case 4:
      orgSize = $tb.val();
      $byte.val(orgSize * square(4));
      $kb.val(orgSize * square(3));
      $mb.val(orgSize * square(2));
      $gb.val(orgSize * square(1));
      break;
  }
}

function square(n) {
  const size = 1024;
  return Math.pow(size, n);
}

function validate(e) {
  const charCode = (e.which) ? e.which : e.keyCode;

  return (charCode > 47 && charCode < 58) || (charCode > 95 && charCode < 106)
      || charCode === 8
      || charCode === 65 || charCode === 67 || charCode === 86 || charCode === 88;
}
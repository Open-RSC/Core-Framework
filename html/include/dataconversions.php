<?php
function usernameToHash($s) {
        $s = strtolower($s);
        $s1 = '';
        for ($i = 0;$i < strlen($s);$i++) {
                $c = $s{$i};
                if ($c >= 'a' && $c <= 'z') {
                    $s1 = $s1 . $c;
                } else if ($c >= '0' && $c <= '9') {
                    $s1 = $s1 . $c;
                } else {
                    $s1 = $s1 . ' ';
                }
        }

        $s1 = trim($s1);
        if (strlen($s1) > 12) {
            $s1 = substr($s1, 0, 12); //trims the username down to 12 characters if more are sent
        }

        $l = 0;
        for ($j = 0;$j < strlen($s1);$j++) {
                $c1 = $s1{$j};
                $l *= 37;
                if ($c1 >= 'a' && $c1 <= 'z') {
                    $l += (1 + ord($c1)) - 97;
                } else if ($c1 >= '0' && $c1 <= '9') {
                    $l += (27 + ord($c1)) - 48;
                }
        }
        return $l;
}
function hashToUsername($l) {
        if ($l < 0) {
                return 'invalid_name';
        }
        $s = '';
        while ($l != 0) {
                $i = floor(floatval($l % 37));
                $l = floor(floatval($l / 37));
                if ($i == 0) {
                    $s = ' ' . $s;
                } 
                else if ($i < 27) {
                        if ($l % 37 == 0) {
                            $s = chr(($i + 65) - 1) . $s;
                        }
                        else {
                                $s = chr(($i + 97) - 1) . $s;
                        }
                }
                else {
                        $s = chr(($i + 48) - 27) . $s;
                }
        }
        return $s;
}
?>
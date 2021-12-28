SELECT oferta_zalacznik.offer_set_id zal_id
,zalacznik.name zal_nazwa
,REGEXP_REPLACE(plik.name, '[^A-Za-z0-9-_\\Ą\\ą\\Ć\\ć\\Ę\\ę\\Ł\\ł\\Ń\\ń\\Ó\\ó\\Ś\\ś\\Ź\\ź\\Ż\\ż\\ ]+', '') zal_plik_nazwa
FROM offer_set_attachment oferta_zalacznik
INNER JOIN attachment zalacznik ON oferta_zalacznik.attachment_id = zalacznik.id
INNER JOIN file plik ON zalacznik.file_id = plik.id
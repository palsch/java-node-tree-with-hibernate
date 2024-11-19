-- analyse simple query
explain (analyse, Buffers)
select *
from node;

-- get some stats from postgres
SELECT datname, usename, query, *
FROM pg_stat_activity;



WITH RECURSIVE child_nodes AS (
    SELECT id, parent_id
    FROM node
    WHERE parent_id IS NOT NULL
--     WHERE id = 'b55f4f25-2161-4ae8-b63d-d3b54b5205f5'::uuid
    UNION ALL
    SELECT n.id, n.parent_id
    FROM node n
             INNER JOIN child_nodes c ON c.id = n.parent_id
)
SELECT a.id, a.status, a.updated_at,
       (
           SELECT COUNT(*)
           FROM child_nodes
--            WHERE parent_id = a.id
           WHERE parent_id = a.id OR id IN (SELECT id FROM child_nodes WHERE parent_id = a.id)
       ) AS node_count
FROM ale_antrag a
WHERE a.id = 'b55f4f25-2161-4ae8-b63d-d3b54b5205f5'::uuid;



select * from node where node.parent_id = 'b55f4f25-2161-4ae8-b63d-d3b54b5205f5';


-- get the count of all child nodes for the given node
WITH RECURSIVE child_nodes AS (
    SELECT id, parent_id
    FROM node
    WHERE id = 'b55f4f25-2161-4ae8-b63d-d3b54b5205f5'::uuid
    UNION ALL
    SELECT n.id, n.parent_id
    FROM node n
             INNER JOIN child_nodes c ON c.id = n.parent_id
)
SELECT COUNT(*)
FROM child_nodes;


-- get the count of all child nodes for the given node
WITH RECURSIVE child_nodes AS (
    SELECT id, parent_id
    FROM node
    WHERE parent_id IS NOT NULL
--     WHERE id = 'b55f4f25-2161-4ae8-b63d-d3b54b5205f5'::uuid
    UNION ALL
    SELECT n.id, n.parent_id
    FROM node n
             INNER JOIN child_nodes c ON c.id = n.parent_id
)

SELECT id, parent_id
FROM node
WHERE id = 'b55f4f25-2161-4ae8-b63d-d3b54b5205f5'::uuid
UNION ALL
SELECT n.id, n.parent_id
FROM node n
         INNER JOIN child_nodes c ON c.id = n.parent_id;
-- SELECT id, parent_id
-- FROM child_nodes;


-- analyse a slow query
explain (analyse, Buffers)
select cn1_0.parent_id,
       cn1_0.id,
       cn1_0.dtype,
       cn1_1.created_at,
       cn1_1.owner_user_id,
       cn1_1.status,
       cn1_1.updated_at,
       cn1_2.name,
       cn1_3.yes_no,
       cn1_4.iban,
       odu1_0.id,
       odu1_0.later_upload,
       odu1_0.owner_user_id,
       rdu1_0.id,
       rdu1_0.later_upload,
       rdu1_0.owner_user_id,
       cn1_5.work_ability_percent,
       cn1_6.yes_no,
       cn1_7.ahv_nummer,
       cn1_7.geburtsdatum,
       cn1_7.nachname,
       odu2_0.id,
       odu2_0.later_upload,
       odu2_0.owner_user_id,
       rdu2_0.id,
       rdu2_0.later_upload,
       rdu2_0.owner_user_id,
       cn1_7.vorname,
       cn1_8.yes_no,
       odu3_0.id,
       odu3_0.later_upload,
       odu3_0.owner_user_id,
       cn1_9.payment,
       cn1_9.payment_type,
       cn1_9.receive_date,
       cn1_9.request_date,
       rdu3_0.id,
       rdu3_0.later_upload,
       rdu3_0.owner_user_id,
       cn1_10.yes_no
from public.node cn1_0
         left join public.ale_antrag cn1_1 on cn1_0.id = cn1_1.id
         left join public.child cn1_2 on cn1_0.id = cn1_2.id
         left join public.child_question cn1_3 on cn1_0.id = cn1_3.id
         left join public.iban_question cn1_4 on cn1_0.id = cn1_4.id
         left join public.work_ability_answer cn1_5 on cn1_0.id = cn1_5.id
         left join public.work_ability_question cn1_6 on cn1_0.id = cn1_6.id
         left join public.child_personal_data_answer cn1_7 on cn1_0.id = cn1_7.id
         left join public.child_personal_data_question cn1_8 on cn1_0.id = cn1_8.id
         left join public.disability_insurance_answer cn1_9 on cn1_0.id = cn1_9.id
         left join public.disability_insurance_question cn1_10 on cn1_0.id = cn1_10.id
         left join public.document_uploads odu1_0 on odu1_0.id = cn1_5.optional_document_upload_id
         left join public.document_uploads rdu1_0 on rdu1_0.id = cn1_5.required_document_upload_id
         left join public.document_uploads odu2_0 on odu2_0.id = cn1_7.optional_document_upload_id
         left join public.document_uploads rdu2_0 on rdu2_0.id = cn1_7.required_document_upload_id
         left join public.document_uploads odu3_0 on odu3_0.id = cn1_9.optional_document_upload_id
         left join public.document_uploads rdu3_0 on rdu3_0.id = cn1_9.required_document_upload_id
where cn1_0.parent_id = '9edcfb16-023a-467d-864a-8fced4daf9b8'


select aa1_0.id, aa1_0.dtype, aa1_0.parent_id, aa1_0.created_at, aa1_0.owner_user_id, aa1_0.status, aa1_0.updated_at
from public.node aa1_0
where aa1_0.dtype = 'ale_antrag'
  and aa1_0.id=?;



explain (analyse, Buffers)
select cn1_0.parent_id,
       cn1_0.id,
       cn1_0.dtype,
       cn1_0.created_at,
       cn1_0.updated_at,
       cn1_1.owner_user_id,
       cn1_1.status,
       cn1_2.yes_no,
       cn1_3.iban,
       odu1_0.id,
       odu1_0.later_upload,
       odu1_0.owner_user_id,
       rdu1_0.id,
       rdu1_0.later_upload,
       rdu1_0.owner_user_id,
       cn1_4.work_ability_percent,
       cn1_5.yes_no,
       cn1_6.ahv_nummer,
       cn1_6.geburtsdatum,
       cn1_6.nachname,
       odu2_0.id,
       odu2_0.later_upload,
       odu2_0.owner_user_id,
       rdu2_0.id,
       rdu2_0.later_upload,
       rdu2_0.owner_user_id,
       cn1_6.vorname,
       cn1_7.yes_no,
       odu3_0.id,
       odu3_0.later_upload,
       odu3_0.owner_user_id,
       cn1_8.payment,
       cn1_8.payment_type,
       cn1_8.receive_date,
       cn1_8.request_date,
       rdu3_0.id,
       rdu3_0.later_upload,
       rdu3_0.owner_user_id,
       cn1_9.yes_no
from public.node cn1_0
         left join public.ale_antrag cn1_1 on cn1_0.id = cn1_1.id
         left join public.child_question cn1_2 on cn1_0.id = cn1_2.id
         left join public.iban_question cn1_3 on cn1_0.id = cn1_3.id
         left join public.work_ability_answer cn1_4 on cn1_0.id = cn1_4.id
         left join public.work_ability_question cn1_5 on cn1_0.id = cn1_5.id
         left join public.child_personal_data_answer cn1_6 on cn1_0.id = cn1_6.id
         left join public.child_personal_data_question cn1_7 on cn1_0.id = cn1_7.id
         left join public.disability_insurance_answer cn1_8 on cn1_0.id = cn1_8.id
         left join public.disability_insurance_question cn1_9 on cn1_0.id = cn1_9.id
         left join public.document_uploads odu1_0 on odu1_0.id = cn1_4.optional_document_upload_id
         left join public.document_uploads rdu1_0 on rdu1_0.id = cn1_4.required_document_upload_id
         left join public.document_uploads odu2_0 on odu2_0.id = cn1_6.optional_document_upload_id
         left join public.document_uploads rdu2_0 on rdu2_0.id = cn1_6.required_document_upload_id
         left join public.document_uploads odu3_0 on odu3_0.id = cn1_8.optional_document_upload_id
         left join public.document_uploads rdu3_0 on rdu3_0.id = cn1_8.required_document_upload_id
where cn1_0.id = 'b55f4f25-2161-4ae8-b63d-d3b54b5205f5'
;

explain(analyse, Buffers)
select *
from node
where parent_id = 'da37fc39-2da7-4b20-8c90-6281361edbcb';

REINDEX TABLE public.node;

SET enable_seqscan = OFF;

ANALYZE node;



select cn3_0.parent_id,
       cn3_0.id,
       cn3_0.dtype,
       cn3_0.created_at,
       cn3_0.updated_at,
       cn3_1.owner_user_id,
       cn3_1.status,
       cn3_2.yes_no,
       cn3_3.iban,
       cn3_4.work_ability_percent,
       cn3_5.yes_no,
       cn3_6.ahv_nummer,
       cn3_6.geburtsdatum,
       cn3_6.nachname,
       cn3_6.vorname,
       cn3_7.yes_no,
       cn3_8.payment,
       cn3_8.payment_type,
       cn3_8.receive_date,
       cn3_8.request_date,
       cn3_9.yes_no
from public.node cn3_0
         left join public.ale_antrag cn3_1 on cn3_0.id = cn3_1.id
         left join public.child_question cn3_2 on cn3_0.id = cn3_2.id
         left join public.iban_question cn3_3 on cn3_0.id = cn3_3.id
         left join public.work_ability_answer cn3_4 on cn3_0.id = cn3_4.id
         left join public.work_ability_question cn3_5 on cn3_0.id = cn3_5.id
         left join public.child_personal_data_answer cn3_6 on cn3_0.id = cn3_6.id
         left join public.child_personal_data_question cn3_7 on cn3_0.id = cn3_7.id
         left join public.disability_insurance_answer cn3_8 on cn3_0.id = cn3_8.id
         left join public.disability_insurance_question cn3_9 on cn3_0.id = cn3_9.id
where cn3_0.parent_id in (select cn2_0.id
                          from public.node cn2_0
                                   left join public.ale_antrag cn2_1 on cn2_0.id = cn2_1.id
                                   left join public.child_question cn2_2 on cn2_0.id = cn2_2.id
                                   left join public.iban_question cn2_3 on cn2_0.id = cn2_3.id
                                   left join public.work_ability_answer cn2_4 on cn2_0.id = cn2_4.id
                                   left join public.work_ability_question cn2_5 on cn2_0.id = cn2_5.id
                                   left join public.child_personal_data_answer cn2_6 on cn2_0.id = cn2_6.id
                                   left join public.child_personal_data_question cn2_7 on cn2_0.id = cn2_7.id
                                   left join public.disability_insurance_answer cn2_8 on cn2_0.id = cn2_8.id
                                   left join public.disability_insurance_question cn2_9 on cn2_0.id = cn2_9.id
                          where cn2_0.parent_id in (select cn1_0.id
                                                    from public.node cn1_0
                                                             left join public.ale_antrag cn1_1 on cn1_0.id = cn1_1.id
                                                             left join public.child_question cn1_2 on cn1_0.id = cn1_2.id
                                                             left join public.iban_question cn1_3 on cn1_0.id = cn1_3.id
                                                             left join public.work_ability_answer cn1_4 on cn1_0.id = cn1_4.id
                                                             left join public.work_ability_question cn1_5 on cn1_0.id = cn1_5.id
                                                             left join public.child_personal_data_answer cn1_6 on cn1_0.id = cn1_6.id
                                                             left join public.child_personal_data_question cn1_7 on cn1_0.id = cn1_7.id
                                                             left join public.disability_insurance_answer cn1_8 on cn1_0.id = cn1_8.id
                                                             left join public.disability_insurance_question cn1_9 on cn1_0.id = cn1_9.id
                                                   where cn1_0.parent_id = 'b55f4f25-2161-4ae8-b63d-d3b54b5205f5'))
;



select cn3_0.parent_id,
       cn3_0.id,
       cn3_0.dtype,
       cn3_0.created_at,
       cn3_0.updated_at,
       cn3_1.owner_user_id,
       cn3_1.status,
       cn3_2.yes_no,
       cn3_3.iban,
       cn3_4.work_ability_percent,
       cn3_5.yes_no,
       cn3_6.ahv_nummer,
       cn3_6.geburtsdatum,
       cn3_6.nachname,
       cn3_6.vorname,
       cn3_7.yes_no,
       cn3_8.payment,
       cn3_8.payment_type,
       cn3_8.receive_date,
       cn3_8.request_date,
       cn3_9.yes_no
from public.node cn3_0
         left join public.ale_antrag cn3_1 on cn3_0.id = cn3_1.id
         left join public.child_question cn3_2 on cn3_0.id = cn3_2.id
         left join public.iban_question cn3_3 on cn3_0.id = cn3_3.id
         left join public.work_ability_answer cn3_4 on cn3_0.id = cn3_4.id
         left join public.work_ability_question cn3_5 on cn3_0.id = cn3_5.id
         left join public.child_personal_data_answer cn3_6 on cn3_0.id = cn3_6.id
         left join public.child_personal_data_question cn3_7 on cn3_0.id = cn3_7.id
         left join public.disability_insurance_answer cn3_8 on cn3_0.id = cn3_8.id
         left join public.disability_insurance_question cn3_9 on cn3_0.id = cn3_9.id
where cn3_0.parent_id in (select cn2_0.id
                          from public.node cn2_0
                          where cn2_0.parent_id in (select cn1_0.id
                                                    from public.node cn1_0
                                                    where cn1_0.parent_id = 'b55f4f25-2161-4ae8-b63d-d3b54b5205f5'))
;






select count(*) from node;


select *
from public.node cn3_0
         left join public.ale_antrag cn3_1 on cn3_0.id = cn3_1.id
         left join public.child_question cn3_2 on cn3_0.id = cn3_2.id
         left join public.iban_question cn3_3 on cn3_0.id = cn3_3.id
         left join public.work_ability_answer cn3_4 on cn3_0.id = cn3_4.id
         left join public.work_ability_question cn3_5 on cn3_0.id = cn3_5.id
         left join public.child_personal_data_answer cn3_6 on cn3_0.id = cn3_6.id
         left join public.child_personal_data_question cn3_7 on cn3_0.id = cn3_7.id
         left join public.disability_insurance_answer cn3_8 on cn3_0.id = cn3_8.id
         left join public.disability_insurance_question cn3_9 on cn3_0.id = cn3_9.id
where cn3_0.parent_id in
(
'5f0b5cde-4bd7-49f2-8481-9cc09f061053',
'8f553ff3-02bd-4581-97bb-26b414bf2048',
'0a4c52a9-100a-4233-8f14-cf6bd904202e',
'9648cae6-b1d4-45d5-bf90-16eb54b2c9e5',
'6958ae25-ea7b-4cd3-bd2e-0db04cab50e6',
'6c481fb0-791e-42e3-90d5-7b35b2e8e395',
'79168cd4-8997-499f-93ea-28deaccc5fd8',
'50c7b16f-d4eb-4a64-bc19-656a688ee126',
'f68c23fe-f126-446f-bc5c-fa21b5b623fe',
'49f2ed9c-564f-4779-bca8-1b2c2573335c',
'9f01c486-2b94-4e64-bcc3-deb2d4fc27b9',
'ec169f47-286a-434b-8914-1d86d78c2bc5',
'875893b2-83e5-4881-9814-bd2badc1f6e5',
'ba3259c8-61a1-43e0-82aa-2092e772938a',
'1eeae33a-0689-4929-b996-4bfcff9e8b4d',
'61970282-72ee-4c68-a69b-5a5561d68ca0',
'37bcdcd2-5fa4-4baf-a7e7-53e7907a2a98')

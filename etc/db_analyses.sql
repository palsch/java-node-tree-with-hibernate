-- analyse simple query
explain  (analyse, Buffers) select * from node;

-- get some stats from postgres
SELECT datname,usename,query, * FROM pg_stat_activity ;

-- analyse a slow query
explain (analyse, Buffers) select cn1_0.parent_id,
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
                           where cn1_0.parent_id = '2751da67-c560-4b9d-86c0-91ee652f3c89'

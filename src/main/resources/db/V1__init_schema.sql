----------------------------------------AUTH---------------------------------------------

CREATE TABLE users (
    user_id UUID NOT NULL,
    password_hash VARCHAR(512) NOT NULL,
    email VARCHAR(512) NOT NULL,
    salt VARCHAR(512) NOT NULL,
    PRIMARY KEY(user_id),
    UNIQUE(email),
    UNIQUE(salt)
);

CREATE TABLE roles (
    user_id UUID NOT NULL,
    name VARCHAR(64) NOT NULL,
    index INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE(user_id, name)
);

------------------------------------------------------------------------------------------

CREATE TABLE intervals (
    user_id UUID NOT NULL,
    number_days INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY(user_id, number_days)
);

----------------------------------------------WORDS---------------------------------------

CREATE TABLE words (
    user_id UUID NOT NULL,
    word_id UUID NOT NULL,
    value VARCHAR(64) NOT NULL,
    note VARCHAR(512),
    repeat_interval_from_english INT NOT NULL,
    last_date_of_repeat_from_english DATE NOT NULL,
    repeat_interval_from_native INT NOT NULL,
    last_date_of_repeat_from_native DATE NOT NULL,
    PRIMARY KEY(word_id),
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE(user_id, value)
);

CREATE TABLE words_interpretations (
    word_id UUID NOT NULL,
    value VARCHAR(512) NOT NULL,
    index INT NOT NULL,
    FOREIGN KEY (word_id) REFERENCES words(word_id) ON DELETE CASCADE,
    UNIQUE(word_id, value)
);

CREATE TABLE words_transcriptions (
    word_id UUID NOT NULL,
    value VARCHAR(128) NOT NULL,
    note VARCHAR(128),
    index INT NOT NULL,
    UNIQUE(word_id, value),
    FOREIGN KEY (word_id) REFERENCES words(word_id) ON DELETE CASCADE
);

CREATE TABLE words_translations (
    word_id UUID NOT NULL,
    value VARCHAR(64) NOT NULL,
    note VARCHAR(128),
    index INT NOT NULL,
    UNIQUE(word_id, value),
    FOREIGN KEY (word_id) REFERENCES words(word_id) ON DELETE CASCADE
);

CREATE TABLE words_examples (
    word_id UUID NOT NULL,
    origin VARCHAR(512) NOT NULL,
    translate VARCHAR(512),
    note VARCHAR(128),
    index INT NOT NULL,
    FOREIGN KEY (word_id) REFERENCES words(word_id) ON DELETE CASCADE,
    UNIQUE(word_id, origin)
);

-----------------------------------WORD-OUTER-SOURCE-BUFFER-----------------------------------

CREATE TABLE word_outer_source (
     word_outer_source_id UUID NOT NULL,
     word_value VARCHAR(64) NOT NULL,
     outer_source_name VARCHAR(64) NOT NULL,
     recent_update_date DATE NOT NULL,
     outer_source_uri VARCHAR(512) NOT NULL,
     PRIMARY KEY(word_outer_source_id),
     UNIQUE(outer_source_name, word_value)
);

CREATE TABLE words_interpretations_outer_source (
    word_outer_source_id UUID NOT NULL,
    interpretation VARCHAR(512) NOT NULL,
    index INT NOT NULL,
    FOREIGN KEY(word_outer_source_id) REFERENCES word_outer_source(word_outer_source_id) ON DELETE CASCADE,
    UNIQUE(word_outer_source_id, interpretation)
);

CREATE TABLE words_transcriptions_outer_source (
    word_outer_source_id UUID NOT NULL,
    transcription VARCHAR(128) NOT NULL,
    index INT NOT NULL,
    FOREIGN KEY(word_outer_source_id) REFERENCES word_outer_source(word_outer_source_id) ON DELETE CASCADE,
    UNIQUE(word_outer_source_id, transcription)
);

CREATE TABLE words_translations_outer_source (
    word_outer_source_id UUID NOT NULL,
    translation VARCHAR(64) NOT NULL,
    index INT NOT NULL,
    FOREIGN KEY(word_outer_source_id) REFERENCES word_outer_source(word_outer_source_id) ON DELETE CASCADE,
    UNIQUE(word_outer_source_id, translation)
);

CREATE TABLE words_examples_outer_source (
    user_id UUID NOT NULL,
    word_outer_source_id UUID NOT NULL,
    example VARCHAR(512) NOT NULL,
    exampleTranslate VARCHAR(512) NOT NULL,
    outer_source_uri_to_example VARCHAR(512) NOT NULL,
    index INT NOT NULL,
    FOREIGN KEY(word_outer_source_id) REFERENCES word_outer_source(word_outer_source_id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE(user_id, word_outer_source_id, example)
);

CREATE VIEW used_words_examples_outer_source
AS
SELECT words_examples_outer_source.user_id as user_id,
       words_examples_outer_source.word_outer_source_id as word_outer_source_id,
       words_examples.origin as example,
       words.value as word_value
    FROM words_examples_outer_source
    INNER JOIN word_outer_source
        ON word_outer_source.word_outer_source_id = words_examples_outer_source.word_outer_source_id
    INNER JOIN words
        ON words.value = word_outer_source.word_value AND
           words.user_id = words_examples_outer_source.user_id
    INNER JOIN words_examples
        ON words_examples.word_id = words.word_id AND
           words_examples.origin = words_examples_outer_source.example;

---------------------------------------------EXPRESSIONS-------------------------------------------------

CREATE TABLE expressions (
    user_id UUID NOT NULL,
    expression_id UUID NOT NULL,
    value VARCHAR(512) NOT NULL,
    note VARCHAR(256) NOT NULL,
    repeat_interval_from_english INT NOT NULL,
    last_date_of_repeat_from_english DATE NOT NULL,
    repeat_interval_from_native INT NOT NULL,
    last_date_of_repeat_from_native DATE NOT NULL,
    PRIMARY KEY(expression_id),
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE(user_id, value)
);

CREATE TABLE expressions_interpretations (
    expression_id UUID NOT NULL,
    value VARCHAR(512) NOT NULL,
    index INT NOT NULL,
    UNIQUE(expression_id, value),
    FOREIGN KEY(expression_id) REFERENCES expressions(expression_id) ON DELETE CASCADE
);

CREATE TABLE expressions_translations (
    expression_id UUID NOT NULL,
    value VARCHAR(64) NOT NULL,
    note VARCHAR(128),
    index INT NOT NULL,
    UNIQUE(expression_id, value),
    FOREIGN KEY(expression_id) REFERENCES expressions(expression_id) ON DELETE CASCADE
);

CREATE TABLE expressions_examples (
    expression_id UUID NOT NULL,
    origin VARCHAR(512) NOT NULL,
    translate VARCHAR(512),
    note VARCHAR(128),
    index INT NOT NULL,
    UNIQUE(expression_id, origin),
    FOREIGN KEY(expression_id) REFERENCES expressions(expression_id) ON DELETE CASCADE
);

-------------------------------------------------STATISTICS-----------------------------------------------

CREATE TABLE repeat_words_from_english_statistic (
    user_id UUID NOT NULL,
    word_id UUID NOT NULL,
    repetition_date DATE NOT NULL,
    is_remember BOOLEAN NOT NULL,
    UNIQUE(user_id, word_id, repetition_date),
    FOREIGN KEY (word_id) REFERENCES words(word_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE repeat_words_from_native_statistic (
    user_id UUID NOT NULL,
    word_id UUID NOT NULL,
    repetition_date DATE NOT NULL,
    is_remember BOOLEAN NOT NULL,
    UNIQUE(user_id, word_id, repetition_date),
    FOREIGN KEY (word_id) REFERENCES words(word_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE repeat_expressions_from_english_statistic (
    user_id UUID NOT NULL,
    expression_id UUID NOT NULL,
    repetition_date DATE NOT NULL,
    is_remember BOOLEAN NOT NULL,
    UNIQUE(user_id, expression_id, repetition_date),
    FOREIGN KEY (expression_id) REFERENCES expressions(expression_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE repeat_expressions_from_native_statistic (
    user_id UUID NOT NULL,
    expression_id UUID NOT NULL,
    repetition_date DATE NOT NULL,
    is_remember BOOLEAN NOT NULL,
    UNIQUE(user_id, expression_id, repetition_date),
    FOREIGN KEY (expression_id) REFERENCES expressions(expression_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE VIEW repeat_words_statistic
AS
SELECT stat.user_id, stat.word_id, stat.repetition_date,
       stat.eng_is_remember, stat.ntv_is_remember, words.value FROM (
    (
        (SELECT eng.user_id, eng.word_id, eng.repetition_date,
                eng.is_remember as eng_is_remember, ntv.is_remember as ntv_is_remember
             from repeat_words_from_english_statistic eng
             LEFT JOIN repeat_words_from_native_statistic ntv
                 ON eng.user_id = ntv.user_id
                    AND eng.word_id = ntv.word_id
                    AND eng.repetition_date = ntv.repetition_date)
        UNION
        (SELECT ntv.user_id, ntv.word_id, ntv.repetition_date,
                eng.is_remember as eng_is_remember, ntv.is_remember as ntv_is_remember
             from repeat_words_from_english_statistic eng
             RIGHT JOIN repeat_words_from_native_statistic ntv
                 ON eng.user_id = ntv.user_id
                    AND eng.word_id = ntv.word_id
                    AND eng.repetition_date = ntv.repetition_date)
    ) AS stat
    INNER JOIN words ON stat.word_id = words.word_id
);

CREATE VIEW repeat_expressions_statistic
AS
SELECT stat.user_id, stat.expression_id, stat.repetition_date,
       stat.eng_is_remember, stat.ntv_is_remember, expressions.value FROM (
    (
        (SELECT eng.user_id, eng.expression_id, eng.repetition_date,
                 eng.is_remember as eng_is_remember, ntv.is_remember as ntv_is_remember
             from repeat_expressions_from_english_statistic eng
             LEFT JOIN repeat_expressions_from_native_statistic ntv
                 ON eng.user_id = ntv.user_id
                    AND eng.expression_id = ntv.expression_id
                    AND eng.repetition_date = ntv.repetition_date)
        UNION
        (SELECT ntv.user_id, ntv.expression_id, ntv.repetition_date,
                eng.is_remember as eng_is_remember, ntv.is_remember as ntv_is_remember
             from repeat_expressions_from_english_statistic eng
             RIGHT JOIN repeat_expressions_from_native_statistic ntv
                 ON eng.user_id = ntv.user_id
                    AND eng.expression_id = ntv.expression_id
                    AND eng.repetition_date = ntv.repetition_date)
    ) AS stat
    INNER JOIN expressions ON stat.expression_id = expressions.expression_id
);

-------------------------------------CUSTOM-FUNCTIONS---------------------------------------------

CREATE ALIAS distance FOR 'com.bakuard.flashcards.dal.impl.StoredProcedures.levenshteinDistance';
CREATE AGGREGATE countTrue FOR 'com.bakuard.flashcards.dal.impl.CountTrue';
CREATE AGGREGATE countFalse FOR 'com.bakuard.flashcards.dal.impl.CountFalse';
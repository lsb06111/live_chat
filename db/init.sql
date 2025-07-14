CREATE TABLE chat_history (
    idx INT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255),
    date_info VARCHAR(255)
);

CREATE TABLE vote (
    idx INT AUTO_INCREMENT PRIMARY KEY,
    vote_content VARCHAR(255),
    date_info VARCHAR(255),
    vote_total VARCHAR(100)
);

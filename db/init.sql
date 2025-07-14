CREATE TABLE chat_history (
    idx INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT,
    date_info VARCHAR(255)
);

CREATE TABLE vote (
    idx INT AUTO_INCREMENT PRIMARY KEY,
    vote_content TEXT,
    date_info VARCHAR(255),
    vote_total TEXT
);

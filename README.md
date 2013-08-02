whotweetedbaros
===============

バルスってつぶやいた人を表示するWebアプリ。http://barosutotubuyaita.herokuapp.com/ のソースです。

フレームワークにはSAStruts+S2JDBCを使ってます。
src/twitter4j.propertiesにTwitterのアクセス情報をいれてください。
DBを使っているので、環境に合わせてjdbc.diconを修正してください。data/create.ddlを使ってテーブルを作成してください。DBはPostgresですが、設定次第で他のも使えるかも。

# ConnectNow - Aplicatie de Social Network
## Obiective
Crearea unei retele sociale. Utilizatorii vor putea sa isi creeze propriile lor conturi, sa isi trimita cereri de prietenie
si in cele din urma sa poata sa vorbeasca cu prietenii lor. Acestia pot crea eventuri la care sa participe si alti utilizatori prieteni cu creatorul eventului, fiind notificati zilnic toti participantii. Utilizatorii vor putea sa vizualizeze diferite rapoarte legate despre activitatea lor pe care si le vor putea exporta in cele din urma pe calculator sub forma de PDF.

## Partea tehnica
Este o aplicatie dezvoltata in Java, folosind drept baza de date PostgreSQL. Pentru interfata grafica a fost folosit JavaFX. Fiind predispusa la un volum mare de date, aplicatia pagineaza informatiile din baza de date, incarcandu-se noi pagini la fiecare scroll in liste.


## Flow
La pornirea aplicatiei, utilizatorul va fi nevoit sa se logheze.
<p align="center">
<img src = "readme-pics/login.PNG">
</p>

<br/><br/>

In cazul in care acesta nu are cont, poate sa isi creeze unul.
<p align="center">
   <img src = "readme-pics/signup.PNG">
</p>

<br/><br/>

Dupa ce utilizatorul se autentifica cu succes, acesta poate naviga pe mai multe taburi si anume:
<br/>

- Show Friends - aici utilizatorul poate sa vada eventurile la care participa si urmeaza sa se apropie, sa isi vada lista de prieteni, si sa isi stearga prietenii

<p align="center">
<img src = "readme-pics/main.PNG">
</p>

<br/><br/>

- Add Friend - aici utilizatorul poate sa caute alti utilizatori si sa ii adauge ca prieteni.

<p align="center">
<img src = "readme-pics/add_friend.PNG">
</p>

<br/><br/>

- Show Requests - unde utilizatorul poate sa accepte cereri de prietene venite de la alte persoane, sau sa isi vada cererile proprii care inca nu au fost acceptate.

<p align="center">
<img src = "readme-pics/requets.PNG">
</p>

<br/><br/>

- Chat Room - unde utilizatorul poate sa isi trimita mesaje cu ceilalti prieteni ai sai.

<p align="center">
<img src = "readme-pics/chat.PNG">
</p>

<br/><br/>

- Reports - unde utilizatorul poate sa isi genereze rapoarte despre activitatea sa pe cont pe care sa si le poata exporta pe calculator sub forma de PDF.

<p align="center">
<img src = "readme-pics/report.PNG">
</p>

<br/><br/>

- Events - unde utilizatorul poate sa creeze eventuri la care pot participa si prietenii sai, de asemenea acesta este notificat de eventurile care urmeaza zilnic, de fiecare data cand intra in cont.

<p align="center">
<img src = "readme-pics/events.PNG">
</p>

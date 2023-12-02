# Certificate creation

## Download and extract CA certificate in pem format from pfx file

## Create new certificate and sign it with CA certificate

# VPN Connection
1. Install openfortivpn on linux.

2. VPN connection only lasts 12h. Therefore we setup a crontab with 8h duration. Create crontab in sudo mode.

`sudo crontab -e`

with following content.

```sh
SHELL=/bin/bash
* */8 * * * /home/erin/bpms/restart-vpn.sh >> /home/erin/bpms/logs/vpn-cron.log 2>&1
```

# nginx config
If big war files are uploaded from admin console (not recommended for now). Increase body size in order to upload bigger files in admin console.
`client_max_body_size 100M;`


# Docker setup 
1. Bpms project -оо ssh githubaas clone хийнэ.
2. github ssh тохируулна.
Заавар:
https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account

3. application build хийн war file – уудаа татаж авна.
Bpms/ docker folder дотор deployments-server, deployments-client гэсэн 2 фолдер үүсгэнэ.
Тухайн 2 фолдерт applicatiion build хийн server, client 3 – н war file – ийг доорх байдлаар
хуулна.
Жич: Зөвхөн war file – ийг хуулна.

4. harbor эрх нээлгэнэ.
Нэвтэрч орон bpms folder дотроос image татна.
bpms project docker folder -т доорх коммандыг бичиж өгсөнөөр images суулгана.
- docker pull harbor.erin.systems/bpms/camunda-bpm-platform:wildfly-7.12.0

- docker pull gvenzl/oracle-xe:11.2.0.2-slim
Заавар:
https://goharbor.io/docs/1.10/working-with-projects/working-with-images/pulling-pushing-images/

5. docker compose up -d command ашиглан проектоо ажиллуулна.
6. docker compose down ашиглан унтраана.
7. docker-compose.yml -д доорх байдлаар харагдана.
8. docker images – ийн хувьд:
- docker image ls

9. openfortivpn install хийж эрх авч, мөн certificate тааруулснаар setup хийж дуусна.
Заавар:
https://just-merwan.medium.com/how-to-install-openfortivpn-in-ubuntu-18-04-3418ae12127b

10. localhost:8080 homepage ruu newtrene.


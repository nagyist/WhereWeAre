usuarios(id,ps)
coordenaas(u_id,fecha,lat,long)

CREATE TABLE iusuarios (
		dni CHAR(9) NOT NULL,
		contraseña VARCHAR(100) NOT NULL,
		CONSTRAINT pk_usuarios PRIMARY KEY (dni))
		
CREATE TABLE ilocalizacion (
	dni CHAR(9) NOT NULL,
	latitud FLOAT NOT NULL,
	longitud FLOAT NOT NULL,
	fecha_hora DATE NOT NULL,
	CONSTRAINT PRIMARY KEY (dni,fecha_hora),
	CONSTRAINT FK_IUSUARIOS FOREIGN KEY(dni) REFERENCES iusuarios(dni)	)
	
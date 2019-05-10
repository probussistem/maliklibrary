# malikkurosakilibrary

__kumpul kumpul__

### to firestore save csv

```java

new MalikKurosaki(this)
                .mintaIjin(300)
                .db(dbf,"data")
                .namaFolderBaru("malikoutput")
                .build();
                
// keterangan

this -> contex
300 -> requestcode for read external storage dont forgate to onRequestPermitionResult() .. on activity
db -> for firestore database ( declare on actifity or fragment)
malikoutput -> name of folder

                
```
__onRequestPermition__

```java
 @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 300){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"membaca external ijin diberikan",Toast.LENGTH_LONG).show();
            }
        }
    }
```

__install__
``` java
dependencies {
	        implementation 'com.github.malikkurosaki:malikkurosakilibrary:1.0'
	}
```

__dependency__

```java

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```


__firebase__

```java
implementation 'com.google.firebase:firebase-core:16.0.8'
implementation 'com.google.firebase:firebase-firestore:18.2.0'
```

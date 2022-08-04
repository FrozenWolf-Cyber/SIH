# Deep Learning models
Models Analyzed :
- [DW_seesawFaceNetv2](https://github.com/cvtower/seesawfacenet_pytorch) (lite model size : 16,611 KB)
- [Reimplementation_seesawnet](https://github.com/pshashk/seesaw-facenet) (5,406 KB)
- [Seesaw_shareFaceNet_160](https://github.com/cvtower/seesawfacenet_pytorch) (5,392 KB)
- [Seesaw_shuffleFaceNet_160](https://github.com/cvtower/seesawfacenet_pytorch) (5,401 KB)
- [Seesaw_shuffleFaceNet_192](https://github.com/cvtower/seesawfacenet_pytorch) (5,401 KB)
- MobileFaceNet (3,180 KB)

## Pretrained Models & training logs & Performance

[seesawfacenet @ googledrive](https://drive.google.com/drive/folders/1n4Zi7YTqG4YoLdK3-aO8qWWEjOCcD7w9?usp=sharing) <br />
[seesawfaceet reimplementation @ googledrive](https://drive.google.com/file/d/1Ub5CI3nqTekLnG1AH1cGrQcwblW5YWoa/edit)


![Image text](https://github.com/cvtower/seesawfacenet_pytorch/raw/master/figures/mobile_version.jpg)
![Image text](https://github.com/cvtower/seesawfacenet_pytorch/raw/master/figures/dw_version.jpg)

Saved Lite Models : [GDrive](https://drive.google.com/drive/folders/1GTdVs5u7H7efcrvsOisbXfTWsJ2hkY6f?usp=sharing), [Github](https://github.com/FrozenWolf-Cyber/SIH/tree/dl_models/saved_models/lite)

No need to normalize the input tensor as the normalization is done inside the model itself, but image resizing and tensor conversion are still required. Threshold I will update later after doing a complete analysis on all models. For now use some placeholder threshold which can be modified later.

```
Input tensor shape : [1, 3, 112, 112] #[batch_size, n_channels, w, h]
Output embedding size : [1, 512]
```


**1) embeddings_DW_seesawFaceNetv2**

![image](https://user-images.githubusercontent.com/57902078/182031058-19103af9-a7e1-4a5e-91b0-d3d1fa8da0d7.png)

```
Best Threshold: 1.2418 with G-Mean: 0.9008
FPR: 0.1754, TPR: 0.984
```

**2) embeddings_reimplementation_seesawnet**

![image](https://user-images.githubusercontent.com/57902078/182031100-da8de5a2-cb4b-42c3-a155-509227e0efe4.png)

```
Best Threshold: 80.4722 with G-Mean: 0.8312
FPR: 0.2156, TPR: 0.8808
```

**3) embeddings_seesaw_shareFaceNet_160**

![image](https://user-images.githubusercontent.com/57902078/182031131-9feb77e4-93d5-431d-bd40-0df7efcbcace.png)

```
Best Threshold: 1.2574 with G-Mean: 0.8494
FPR: 0.2204, TPR: 0.9255
```

**4) embeddings_seesaw_shuffleFaceNet_160**

![image](https://user-images.githubusercontent.com/57902078/182031160-0476e9e6-637c-4072-aa7d-7bf049771545.png)

```
Best Threshold: 1.2461 with G-Mean: 0.8616
FPR: 0.2126, TPR: 0.9427
```

**5) embeddings_seesaw_shuffleFaceNet_192**

![image](https://user-images.githubusercontent.com/57902078/182031174-c6ef10c0-7507-4cad-815a-84dd219a10e5.png)

```
Best Threshold: 1.2628 with G-Mean: 0.8678
FPR: 0.1963, TPR: 0.937

```

**6) MobileFaceNet**

![image](https://user-images.githubusercontent.com/57902078/182843910-33aa50a3-8c93-4aa2-9b82-09c5bacde5b4.png)

```
Best Threshold: 48.703 with G-Mean: 0.85
FPR: 0.1579, TPR: 0.8579

```

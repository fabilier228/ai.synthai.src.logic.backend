from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch
import torch.nn.functional as F

# Ścieżka do Twojego wytrenowanego modelu
MODEL_DIR = "./results/xlmroberta_pl_en"

# Wczytanie modelu i tokenizera (kompatybilne z różnymi modelami)
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
tokenizer = AutoTokenizer.from_pretrained(MODEL_DIR)
model = AutoModelForSequenceClassification.from_pretrained(MODEL_DIR)
model.to(device)
model.eval()


def classify_text(texts, batch_size=16, return_probs=False, threshold=None):
    """
    texts: str or list[str]
    return_probs: if True returns dicts with label and probability distribution
    threshold: optional float in (0,1); if provided and max_prob < threshold, label will be 'niepewne'
    """
    single = False
    if isinstance(texts, str):
        single = True
        texts = [texts]

    # sanitize inputs
    texts = [t if (isinstance(t, str) and t.strip() != "") else "<EMPTY>" for t in texts]

    results = []
    for i in range(0, len(texts), batch_size):
        batch = texts[i:i+batch_size]
        enc = tokenizer(batch, truncation=True, padding=True, max_length=128, return_tensors="pt")
        enc = {k: v.to(device) for k, v in enc.items()}
        with torch.no_grad():
            out = model(**enc)
            logits = out.logits
            probs = F.softmax(logits, dim=-1).cpu()
            preds = torch.argmax(probs, dim=-1).cpu().tolist()
            for p_idx, prob in zip(preds, probs.tolist()):
                max_prob = max(prob)
                if threshold is not None and max_prob < threshold:
                    label = "niepewne"
                else:
                    label = "pozytywny" if p_idx == 1 else "negatywny"
                if return_probs:
                    results.append({"label": label, "probs": prob})
                else:
                    results.append(label)

    return results[0] if single else results


if __name__ == "__main__":
    # szybki sanity-check
    examples = ["To jest świetny produkt, polecam!", "Bardzo złe, nie polecam."]
    print(classify_text(examples, return_probs=True))
